package com.demo.controller;

import com.demo.domain.ProductScoreEntity;
import com.demo.service.RecommandService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
public class RecommandController {

    @Autowired
    RecommandService recommandService;

    @GetMapping("/recommand")
    @ResponseBody
    public List<ProductScoreEntity> recommandByUserId(@Param("userId") String userId) throws IOException {
        return recommandService.userRecommand(userId);
    }

    @GetMapping("/user")
    public String recommandForUser(@RequestParam("userId") String userId,
                                   Model model){
        return "user";
    }
}
