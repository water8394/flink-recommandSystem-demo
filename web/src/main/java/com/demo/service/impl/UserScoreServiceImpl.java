package com.demo.service.impl;

import com.demo.client.HbaseClient;
import com.demo.entity.UserScoreEntity;
import com.demo.service.UserScoreService;

import java.io.IOException;

/**
 * @author XINZE
 */
public class UserScoreServiceImpl implements UserScoreService {
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
        return null;


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
        int blcak = getValue(userId,"blcak");
        int brown = getValue(userId,"brown");
        int grey = getValue(userId,"grey");
        return getPercent(red,green,blcak,brown,grey);
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
}
