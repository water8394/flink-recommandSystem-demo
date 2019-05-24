package com.demo.service;

import com.demo.entity.UserScoreEntity;

import java.io.IOException;

public interface UserScoreService {

    public UserScoreEntity calUserScore(String userId) throws IOException;
}
