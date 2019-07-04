package com.demo.controller;

import com.demo.client.RedisClient;
import com.demo.util.Result;
import com.demo.util.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BackstageController {

    private RedisClient redisClient = new RedisClient();

    private int topSize = 10;

    /**
     * 获取redis中存取的top榜单
     * @return json
     */
    @GetMapping
    public Result topProduct(){
        List<String> topList = redisClient.getTopList(topSize);
        return ResultUtils.success(topList);
    }

}
