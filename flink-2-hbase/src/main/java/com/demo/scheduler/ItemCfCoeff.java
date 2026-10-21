package com.demo.scheduler;

import com.demo.client.HbaseClient;
import com.demo.util.Similarity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基于协同过滤的产品相关度计算
 * * 策略1 ：协同过滤
 *      *           abs( i ∩ j)
 *      *      w = ——————————————
 *      *           sqrt(i || j)
 * @author XINZE
 */
public class ItemCfCoeff {

    /**
     * 计算一个产品和其他相关产品的评分,并将计算结果放入Hbase
     *
     * @param id     产品id
     * @param others 其他产品的id
     */
    public void getSingelItemCfCoeff(String id, List<String> others) throws Exception {

        for (String other : others) {
        	if(id.equals(other)) continue;
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
        List<Object> left = new ArrayList<>();
        List<Object> right = new ArrayList<>();
        for (Map.Entry entry : p1) {
            left.add(entry.getKey());
        }
        for (Map.Entry entry : p2) {
            right.add(entry.getKey());
        }
        return Similarity.itemCfScore(left, right);
    }


}
