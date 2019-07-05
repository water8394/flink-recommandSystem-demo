package com.demo.service;

import com.demo.domain.ProductScoreEntity;

import java.io.IOException;
import java.util.List;

public interface RecommandService {


    /**
     * 基于用户特征的热度表和产品标签关联表 -> 联合推荐
     * @param userId
     * @return
     * @throws IOException
     */
    public List<ProductScoreEntity> userRecommand(String userId) throws IOException;
}
