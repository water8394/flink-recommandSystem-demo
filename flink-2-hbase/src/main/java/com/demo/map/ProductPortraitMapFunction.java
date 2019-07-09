package com.demo.map;

import com.demo.client.HbaseClient;
import com.demo.client.MysqlClient;
import com.demo.domain.LogEntity;
import com.demo.util.AgeUtil;
import com.demo.util.LogToEntity;
import org.apache.flink.api.common.functions.MapFunction;

import java.sql.ResultSet;

/**
 * @author XINZE
 */
public class ProductPortraitMapFunction implements MapFunction<String, String> {
    @Override
    public String map(String s) throws Exception {
        LogEntity log = LogToEntity.getLog(s);
        ResultSet rst = MysqlClient.selectUserById(log.getUserId());
        if (rst != null){
            while (rst.next()){
                String productId = String.valueOf(log.getProductId());
                String sex = rst.getString("sex");
                HbaseClient.increamColumn("prod",productId,"sex",sex);
                String age = rst.getString("age");
                HbaseClient.increamColumn("prod",productId,"age", AgeUtil.getAgeType(age));
            }
        }
        return null;
    }
}
