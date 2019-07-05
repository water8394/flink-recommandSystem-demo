package com.demo.service;

import com.demo.domain.ProductEntity;
import com.demo.domain.ProductScoreEntity;
import com.demo.domain.UserScoreEntity;
import java.util.List;
import java.io.IOException;

public interface UserScoreService {

    /**
     * 通过用户id计算其特征评分
     *
     *             颜色
     * ------------------------
     * 标签    红    绿    蓝
     * 次数    20    30   50
     * ------------------------
     * 得分    0.2  0.3   0.5
     *
     * @param userId 用户id
     * @return
     * @throws IOException
     */
    public UserScoreEntity calUserScore(String userId) throws IOException;

    /**
     * 通过用户特征分数 计算推荐产品列表
     * @param userScore 用户特征分数对象
     * @return
     */
    public List<ProductScoreEntity> getProductScore(UserScoreEntity userScore);

    /**
     * 根据用户id和其特征评分 重新排序热度榜并返回
     * @param userid 用户id
     * @return
     * @throws IOException
     */
    public List<ProductScoreEntity> getTopRankProduct(String userid) throws IOException;

    /**
     * 将产品id list 转为产品实体类list
     * @param products
     * @return
     */
    public List<ProductEntity> getTopProductFrom(List<String> products);
}
