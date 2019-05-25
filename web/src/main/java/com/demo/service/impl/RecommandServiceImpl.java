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

    @Override
    public List<ProductScoreEntity> userRecommand(String userId) throws IOException {
        List<ProductScoreEntity> randProduct = userScoreService.getTopRankProduct(userId);
        randProduct.sort((a, b) -> {
            Double compare;
//            if (a.getRank() == 0 || b.getRank() == 0){
//                 compare = a.getScore() - b.getScore();
//                return compare.intValue();
//            }

            compare = (1 / (a.getRank()+1) + a.getScore()) - (1 / (a.getRank()+1) + b.getScore());
            return compare.intValue();
        });
        return randProduct;
    }

}
