package com.demo.service.impl;

import com.demo.client.HbaseClient;
import com.demo.entity.ProductEntity;
import com.demo.entity.ProductScoreEntity;
import com.demo.service.ProductService;
import com.demo.service.RecommandService;
import com.demo.service.UserScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("recommandService")
public class RecommandServiceImpl implements RecommandService {

    @Autowired
    UserScoreService userScoreService;
    @Autowired
    ProductService productService;


    private double BASE_SCORE = 0.5;

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

}
