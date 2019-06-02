package com.demo.client;

import com.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class RedisClient {
    private Jedis jedis = new Jedis("192.168.0.100", 6379);

    @Autowired
    private ProductService productService;


    private static RedisClient redisClient;



    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        redisClient = this;
        redisClient.productService = this.productService;
    }

    private String getData(String key){
        return jedis.get(key);
    }

    public List<String> getTopList(int topRange){
        List<String> res = new ArrayList<>();

        for (int i = 0; i < topRange; i++) {
            res.add(getData(String.valueOf(i)));
        }
        return res;
    }


    public static void main(String[] args) {
        RedisClient client = new RedisClient();

        String data = client.getData("1");
        System.out.println(data);
    }
}
