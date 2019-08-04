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
import org.apache.hadoop.hbase.client.coprocessor.BigDecimalColumnInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
			if (compare > 0) {
				return -1;
			} else {
				return 1;
			}
		});
		List<ProductScoreEntity> rst = new ArrayList<>();
		randProduct.forEach(r -> {
			try {
				rst.add(r);
				List<Map.Entry> ps = HbaseClient.getRow("ps", userId);
				int end = ps.size() > 3 ? ps.size() : 3;
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
		List<String> topList = getDefaultTop();
		int topSize = topList.size();
		// 拿到产品详情表
		List<ContactEntity> contactEntities = contactService.selectByIds(topList);
		// 拿到产品基本信息表
		List<ProductEntity> productEntities = productService.selectByIds(topList);
		return fillProductDto(topList, contactEntities, productEntities, topSize);
	}

	@Override
	public List<ProductDto> recomandByItemCfCoeff() throws IOException {
		List<String> topList = getDefaultTop();
		List<String> px = addRecommandProduct(topList, "px");
		px = removeDuplicateWithOrder(px);
		// 拿到产品详情表
		List<ContactEntity> contactEntities = contactService.selectByIds(px);
		// 拿到产品基本信息表
		List<ProductEntity> productEntities = productService.selectByIds(px);
		return transferToDto(px, contactEntities, productEntities);
	}


	@Override
	public List<ProductDto> recomandByProductCoeff() throws IOException {
		List<String> topList = getDefaultTop();
		List<String> ps = addRecommandProduct(topList, "ps");
		ps = removeDuplicateWithOrder(ps);
		// 拿到产品详情表
		List<ContactEntity> contactEntities = contactService.selectByIds(ps);
		// 拿到产品基本信息表
		List<ProductEntity> productEntities = productService.selectByIds(ps);
		return transferToDto(ps, contactEntities, productEntities);
	}


	/**
	 * 根据id 包装产品dto类
	 * @param id
	 * @return
	 */
	private ProductDto selectProductById(String id) {
		ProductEntity productEntity = productService.selectById(id);
		ContactEntity contactEntity = contactService.selectById(id);
		ProductDto dto = new ProductDto();
		dto.setProduct(productEntity);
		dto.setContact(contactEntity);
		return dto;
	}

	/**
	 * 查询中对应的hbase推荐表数据添加加结果集
	 * @param topList
	 * @return List<String> 结果id集合
	 */
	private List<String> addRecommandProduct(List<String> topList, String table) {
		List<String> ret = new ArrayList<>();
		for (String s : topList) {
			//首先将top产品添加进结果集
			ret.add(s);
			List<Map.Entry> ps = new ArrayList<>();
			//获取的产品list是已经排好序的,根据得分排序
			try {
				ps = HbaseClient.getRow(table, s);
				Collections.sort(ps,((o1, o2) -> -(new BigDecimal(o1.getValue().toString()).compareTo(new BigDecimal(o2.getValue().toString())))));
			} catch (Exception e) {
				logger.warn("Hbase中没有产品【{}】记录", s);
			}
			if (CollectionUtils.isEmpty(ps)) {
				continue;
			}
			// 只保留最相关的3个产品
			int end = Math.min(ps.size(), PRODUCT_LIMIT);
			for (int i = 0; i < end; i++) {
				if (Objects.nonNull(ps.get(i))) {
					ret.add((String) ps.get(i).getKey());
				}

			}
		}
		return ret;
	}

	/**
	 * 将
	 * @param list
	 * @param contactEntities
	 * @param productEntities
	 * @return
	 */
	private List<ProductDto> transferToDto(List<String> list, List<ContactEntity> contactEntities,
			List<ProductEntity> productEntities) {
		int topSize = Math.min(Math.min(list.size(), contactEntities.size()), productEntities.size());
		return fillProductDto(list, contactEntities, productEntities, topSize);
	}

	/**
	 * 将产品转为dto
	 * @param list
	 * @param contactEntities
	 * @param productEntities
	 * @param topSize
	 * @return
	 */
	private List<ProductDto> fillProductDto(List<String> list, List<ContactEntity> contactEntities,
			List<ProductEntity> productEntities, int topSize) {
		List<ProductDto> ret = new ArrayList<>();
		for (int i = 0; i < topSize; i++) {
			String topId = list.get(i);
			ProductDto dto = new ProductDto();
			dto.setScore(TOP_SIZE + 1 - i);
			for (int j = 0; j < topSize; j++) {
				if (topId.equals(String.valueOf(contactEntities.get(j).getId()))) {
					dto.setContact(contactEntities.get(j));
				}
				if (topId.equals(String.valueOf(productEntities.get(j).getProductId()))) {
					dto.setProduct(productEntities.get(j));
				}
			}
			ret.add(dto);
		}
		return ret;
	}

	/**
	 * 删除list中重复元素
	 * @param list
	 */
	public static <T> List<T> removeDuplicateWithOrder(List<T> list) {
		return list.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * 如果没有达到TOP_SIZE，就从数据库中取补充至TOP_SIZE
	 * @return
	 */
	private List<String> getDefaultTop() {
		List<String> topList = redisClient.getTopList(TOP_SIZE);
		topList = topList.stream().filter(Objects::nonNull).collect(Collectors.toList());
		if (topList.size() < 10) {
			// 尽量多的拿产品列表
			topList.addAll(productService.selectInitPro(100));
			topList = topList.stream().distinct().collect(Collectors.toList());
			logger.info("top: {}", topList);
		}
		return topList;
	}


}
