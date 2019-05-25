package com.demo.controller;

import com.demo.entity.ProductScoreEntity;
import com.demo.service.RecommandService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class RecommandController {

    @Autowired
    RecommandService recommandService;

    @GetMapping("/recommand")
    public List<ProductScoreEntity> recommandByUserId(@Param("userId") String userId) throws IOException {
        return recommandService.userRecommand(userId);
    }
}
