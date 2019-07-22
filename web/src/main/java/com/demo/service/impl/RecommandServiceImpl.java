package com.demo.service.impl;

import com.demo.client.HbaseClient;
import com.demo.client.RedisClient;
import com.demo.domain.ContactEntity;
import com.demo.domain.ProductEntity;
import com.demo.domain.ProductScoreEntity;
import com.demo.dto.ProductDto;
import com.demo.service.ContactService;
import com.demo.service.ProductService;
import com.demo.service.RecommandService;
import com.demo.service.UserScoreService;
import org.apache.flink.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service("recommandService")
public class RecommandServiceImpl implements RecommandService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserScoreService userScoreService;
    @Autowired
    ProductService productService;
    @Autowired
    ContactService contactService;
	@Resource
	private RedisClient redisClient;

    private final static int TOP_SIZE = 10;   // 热度榜产品数
    private final static int PRODUCT_LIMIT = 3;  // 相关产品数

    @Override
    public List<ProductScoreEntity> userRecommand(String userId) throws IOException {
        List<ProductScoreEntity> randProduct = userScoreService.getTopRankProduct(userId);
        randProduct.sort((a, b) -> {
            Double compare;
            compare = a.getScore() - b.getScore();
            if (compare > 0){
                return -1;
            }else {
                return 1;
            }
        });
        List<ProductScoreEntity> rst = new ArrayList<>();
        randProduct.forEach(r->{
            try {
                rst.add(r);
                List<Map.Entry> ps = HbaseClient.getRow("ps", userId);
                int end = ps.size()>3 ? ps.size() : 3;
                for (int i = 0; i < end; i++) {
                    Map.Entry entry = ps.get(i);
                    ProductEntity p = productService.selectById((String) entry.getKey());
                    ProductScoreEntity pWithScore = new ProductScoreEntity();
                    pWithScore.setProduct(p);
                    pWithScore.setScore(r.getScore());
                    pWithScore.setRank(r.getRank());
                    rst.add(pWithScore);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return rst;
    }

    @Override
    public List<ProductDto> recommandByHotList() {
        // 获取top榜单
        List<String> topList = redisClient.getTopList(TOP_SIZE);
        // 拿到产品详情表
        List<ContactEntity> contactEntities = contactService.selectByIds(topList);
        // 拿到产品基本信息表
        List<ProductEntity> productEntities = productService.selectByIds(topList);
        List<ProductDto> ret = new ArrayList<>();
        // 将产品转为dto
        for (int i = 0; i < TOP_SIZE; i++) {
            String topId = topList.get(i);
            ProductDto dto = new ProductDto();
            dto.setScore(11-i);
            for (int j = 0; j < TOP_SIZE; j++) {
                if (topId.equals(String.valueOf(contactEntities.get(j).getId()))){
                    dto.setContact(contactEntities.get(j));
                }
                if (topId.equals(String.valueOf(productEntities.get(j).getProductId()))){
                    dto.setProduct(productEntities.get(j));
                }
            }
            ret.add(dto);
        }
        return ret;
    }

    @Override
    public List<ProductDto> recomandByItemCfCoeff() throws IOException {
        List<String> topList = redisClient.getTopList(TOP_SIZE);
        List<ProductDto> dtoList = new ArrayList<>();
        for (String s : topList) {
			List<Map.Entry> ps = new ArrayList<>();
			try {
				ps = HbaseClient.getRow("px", s);
			} catch (Exception e) {
				logger.warn("px 没有产品【{}】记录", s);
			}
			if(CollectionUtils.isEmpty(ps)){
				continue;
			}
            // 只保留最相关的3个产品
            int end = ps.size()>PRODUCT_LIMIT ? ps.size() : PRODUCT_LIMIT;
            for (int i = 0; i < end; i++) {
                dtoList.add(selectProductById((String) ps.get(i).getKey()));
            }
        }
        return dtoList;
    }

    @Override
    public List<ProductDto> recomandByProductCoeff() throws IOException {
        List<String> topList = redisClient.getTopList(TOP_SIZE);
        List<ProductDto> dtoList = new ArrayList<>();
        for (String s : topList) {

			List<Map.Entry> ps = new ArrayList<>();
			//获取的产品list是已经排好序的,根据得分排序
			try {
				ps = HbaseClient.getRow("ps", s);
			} catch (Exception e) {
				logger.warn("ps 没有产品【{}】记录", s);
			}
			if(CollectionUtils.isEmpty(ps)){
				continue;
			}

            // 只保留最相关的3个产品
            int end = ps.size()>PRODUCT_LIMIT ? ps.size() : PRODUCT_LIMIT;
            for (int i = 0; i < end; i++) {
                dtoList.add(selectProductById((String) ps.get(i).getKey()));
            }
        }
        return dtoList;
    }


    /**
     * 根据id 包装产品dto类
     * @param id
     * @return
     */
    private ProductDto selectProductById(String id){
        ProductEntity productEntity = productService.selectById(id);
        ContactEntity contactEntity = contactService.selectById(id);
        ProductDto dto = new ProductDto();
        dto.setProduct(productEntity);
        dto.setContact(contactEntity);
        return dto;
    }
}
