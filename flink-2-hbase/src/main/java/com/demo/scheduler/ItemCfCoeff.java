package com.demo.scheduler;

import com.demo.client.HbaseClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基于协同过滤的产品相关度计算
 * @author XINZE
 */
public class ItemCfCoeff {

    /**
     * 计算一个产品和其他相关产品的评分,并将计算结果放入Hbase
     *
     * @param id     产品id
     * @param others 其他产品的id
     */
    public void getSingelItemCfCoeff(String id, String[] others) throws Exception {

        List<Map<String, Double>> scores = new ArrayList<>();
        for (String other : others) {
            Double score = twoItemCfCoeff(id, other);
            HbaseClient.putData("px",id, "p",other,score.toString());
        }
    }

    /**
     * 计算两个产品之间的评分
     * @param id
     * @param other
     * @return
     * @throws IOException
     */
    private Double twoItemCfCoeff(String id, String other) throws IOException {
        List<Map.Entry> p1 = HbaseClient.getRow("p_history", id);
        List<Map.Entry> p2 = HbaseClient.getRow("p_history", other);

        int n = p1.size();
        int m = p2.size();
        int sum = 0;
        Double total = Math.sqrt(n * m);
        for (Map.Entry entry : p1) {
            String key = (String) entry.getKey();
            for (Map.Entry p : p2) {
                if (key == (String) p.getKey()) {
                    sum++;
                }
            }
        }
        if (total == 0){
            return 0.0;
        }
        return sum/total;

    }


}
