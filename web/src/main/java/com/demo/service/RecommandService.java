package com.demo.service;

import com.demo.entity.ProductScoreEntity;

import java.io.IOException;
import java.util.List;

public interface RecommandService {

    public List<ProductScoreEntity> userRecommand(String userId) throws IOException;
}
