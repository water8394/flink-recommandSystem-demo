package com.demo.service;

import com.demo.domain.ProductScoreEntity;
import com.demo.dto.ProductDto;

import java.io.IOException;
import java.util.List;

public interface RecommandService {


    /**
     * 弃用
     *
     * 基于用户特征的热度表和产品标签关联表 -> 联合推荐
     * @param userId
     * @return
     * @throws IOException
     */
    List<ProductScoreEntity> userRecommand(String userId) throws IOException;

    /**
     * 热度榜数据
     */
    List<ProductDto> recommandByHotList();

    /**
     * 协同过滤推荐结果
     * @return
     */
    List<ProductDto> recomandByItemCfCoeff() throws IOException;

    /**
     * 产品画像推荐结果
     * @return
     */
    List<ProductDto> recomandByProductCoeff() throws IOException;
}
