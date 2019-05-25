package com.demo.service;

import com.demo.entity.ProductEntity;
import com.demo.entity.ProductScoreEntity;
import com.demo.entity.UserScoreEntity;
import java.util.List;
import java.io.IOException;

public interface UserScoreService {

    public UserScoreEntity calUserScore(String userId) throws IOException;

    public List<ProductScoreEntity> getProductScore(UserScoreEntity userScore);

    /**
     * 获取top产品以及用户的评分
     * @param userid
     * @return
     * @throws IOException
     */
    public List<ProductScoreEntity> getTopRankProduct(String userid) throws IOException;

    public List<ProductEntity> getTopProductFrom(List<String> products);
}
