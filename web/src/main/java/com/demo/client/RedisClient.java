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
    private Jedis jedis = new Jedis("192.168.124.110", 6379);

    @Autowired
    private ProductService productService;


    private static RedisClient redisClient;

    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        redisClient = this;
        redisClient.productService = this.productService;
    }

    /**
     * 获取redis数据
     * @param key
     * @return
     */
    private String getData(String key){
        return jedis.get(key);
    }

    /**
     * 获取 top 榜单
     * @param topRange
     * @return
     */
    public List<String> getTopList(int topRange){
        List<String> res = new ArrayList<>();

        for (int i = 0; i < topRange; i++) {
            res.add(getData(String.valueOf(i)));
        }
        return res;
    }

    /**
     * 获取1小时内接入量数据
     * @return
     */
    public String getMeter(){
        return getData("meter");
    }


    /**
     * 测试方法
     * @param args
     */
    public static void main(String[] args) {
        RedisClient client = new RedisClient();

        String data = client.getData("1");
        System.out.println(data);
    }
}
