package com.demo.client;


import redis.clients.jedis.Jedis;
import java.util.ArrayList;
import java.util.List;

public class RedisClient {
    private Jedis jedis = new Jedis("192.168.0.100", 6379);

    private static RedisClient redisClient;


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
