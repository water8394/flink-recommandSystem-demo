package com.demo.controller;

import com.demo.client.RedisClient;
import com.demo.domain.ProductEntity;
import com.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BackstageController {

    private RedisClient redisClient = new RedisClient();

    private int topSize = 10;

    @Autowired
    ProductService productService;

    /**
     * 获取后台数据
     * @return json
     */
    @GetMapping("/index")
    public String getBackStage(Model model){
        List<String> topList = redisClient.getTopList(topSize);
        System.out.println(topList);
        List<ProductEntity> topProduct = productService.selectByIds(topList);
        model.addAttribute("topProduct", topProduct);
        return "index";
    }
}
