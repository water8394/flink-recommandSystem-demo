package com.demo.service.impl;

import com.demo.client.HbaseClient;
import com.demo.client.RedisClient;
import com.demo.domain.ProductEntity;
import com.demo.domain.ProductScoreEntity;
import com.demo.domain.UserScoreEntity;
import com.demo.service.ProductService;
import com.demo.service.UserScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XINZE
 */
@Service("userScoreService")
public class UserScoreServiceImpl implements UserScoreService {

    private RedisClient redisClient = new RedisClient();
    @Autowired
    ProductService productService;
    /**
     * 计算用户的得分
     * @param userId
     * @return
     * @throws IOException
     */
    @Override
    public UserScoreEntity calUserScore(String userId) throws IOException {

        Double[] colors = calColor(userId);
        Double[] countrys = calCountry(userId);
        Double[] styles = calStyle(userId);
        UserScoreEntity userScoreEntity = new UserScoreEntity();
        userScoreEntity.setUserId(userId);
        userScoreEntity.setColor(colors);
        userScoreEntity.setCountry(countrys);
        userScoreEntity.setStyle(styles);
        return userScoreEntity;


    }

    @Override
    public List<ProductScoreEntity> getProductScore(UserScoreEntity userScore) {
        List<ProductScoreEntity> res = new ArrayList<>();
        List<ProductEntity> topProduct = getTopProductFrom(redisClient.getTopList(10));
        int i = 0;
        for (ProductEntity entity : topProduct){
            if (null != entity){
                ProductScoreEntity score = new ProductScoreEntity();
                double v = calProduct(userScore, entity);
                score.setScore(v);
                score.setRank(i++);
                score.setProduct(entity);
                res.add(score);
            }
        }
        return res;
    }

    @Override
    public List<ProductScoreEntity> getTopRankProduct(String userid) throws IOException {
        UserScoreEntity userScore = calUserScore(userid);
        return getProductScore(userScore);
    }

    @Override
    public List<ProductEntity> getTopProductFrom(List<String> products) {
        List<ProductEntity> top = new ArrayList<>();
        for (String id : products){
            ProductEntity entity;
            if (null != id){
                entity = productService.selectById(id);
                top.add(entity);
            }else{
                top.add(null);
            }
        }
        return top;
    }


    private Double[] calStyle(String userId) throws IOException {
        int style0 = getValue(userId,"0");
        int style1 = getValue(userId,"1");
        int style2 = getValue(userId,"2");
        int style3 = getValue(userId,"3");
        int style4 = getValue(userId,"4");
        int style5 = getValue(userId,"5");
        int style6 = getValue(userId,"6");

        return getPercent(style0, style1, style2, style3, style4, style5);
    }


    private Double[] calCountry(String userId) throws IOException {
        int china = getValue(userId,"china");
        int japan = getValue(userId,"japan");
        int korea = getValue(userId,"korea");

        return getPercent(china,japan,korea);
    }

    private Double[] calColor(String userId) throws IOException {

        int red = getValue(userId,"red");
        int green = getValue(userId,"green");
        int black = getValue(userId,"black");
        int brown = getValue(userId,"brown");
        int grey = getValue(userId,"grey");
        return getPercent(red,green,black,brown,grey);
    }


    private int getValue(String userId, String valueName) throws IOException {
        String value = HbaseClient.getData("user", userId, "style", valueName);
        int res = 0;
        if (null != value){
            res = Integer.valueOf(value);
        }
        return res;
    }

    private Double[] getPercent(int... v){
        int size = v.length;
        double total = 0.0;
        Double[] res = new Double[size];
        for (int i = 0; i < size; i++) {
            total += v[i];
        }
        if (total == 0){
            for (int j = 0; j < size; j++) {
                res[j] = 0.0;
                return res;
            }
        }
        for (int i = 0; i < size; i++) {
            res[i] = v[i] / total;
        }
        return res;
    }

    private double calProduct(UserScoreEntity userScore, ProductEntity entity){
        String color = entity.getColor();
        String country = entity.getCountry();
        String style = entity.getStyle();
        double val = 0.0;
        switch (color){
            case "red":
                val += userScore.getColor()[0]== null ? 0 :userScore.getColor()[0];
                break;
            case "green":
                val += userScore.getColor()[1]== null ? 0 :userScore.getColor()[1];
                break;
            case "black":
                val += userScore.getColor()[2]== null ? 0 :userScore.getColor()[2];
                break;
            case "brown":
                val += userScore.getColor()[3]== null ? 0 :userScore.getColor()[3];
                break;
            case "grey":
                val += userScore.getColor()[4]== null ? 0 :userScore.getColor()[4];
                break;
            default:
                val += 0;
                break;
        }
        switch (country){
            case "china":
                val += userScore.getCountry()[0] == null ? 0 :userScore.getCountry()[0];
                break;
            case "japan":
                val +=userScore.getCountry()[1] == null ? 0 :userScore.getCountry()[1];
                break;
            case "korea":
                val += userScore.getCountry()[2] == null ? 0 :userScore.getCountry()[2];
                break;
            default:
                val += 0;
                break;
        }
        switch (style){
            case "0":
                val += userScore.getStyle()[0] == null ? 0 :userScore.getStyle()[0];
                break;
            case "1":
                val += userScore.getStyle()[1] == null ? 0 :userScore.getStyle()[1];
                break;
            case "2":
                val += userScore.getStyle()[2] == null ? 0 :userScore.getStyle()[2];
                break;
            case "3":
                val += userScore.getStyle()[3] == null ? 0 :userScore.getStyle()[3];
                break;
            case "4":
                val +=userScore.getStyle()[4] == null ? 0 :userScore.getStyle()[4];
                break;
            case "5":
                val += userScore.getStyle()[5] == null ? 0 :userScore.getStyle()[5];
                break;
            default:
                val += 0;
                break;
        }

        return val;
    }
}
