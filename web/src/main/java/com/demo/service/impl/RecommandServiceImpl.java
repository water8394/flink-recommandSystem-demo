package com.demo.service.impl;

import com.demo.entity.ProductScoreEntity;
import com.demo.service.RecommandService;
import com.demo.service.UserScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service("recommandService")
public class RecommandServiceImpl implements RecommandService {

    @Autowired
    UserScoreService userScoreService;

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
        return randProduct;
    }

}
