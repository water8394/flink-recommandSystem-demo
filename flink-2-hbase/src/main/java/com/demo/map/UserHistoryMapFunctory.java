package com.demo.map;

import com.demo.client.HbaseClient;
import com.demo.entity.LogEntity;
import com.demo.util.LogToEntity;
import org.apache.flink.api.common.functions.MapFunction;

/**
 * @author XINZE
 */
public class UserHistoryMapFunctory implements MapFunction<String, String> {


    @Override
    public String map(String s) throws Exception {
        LogEntity log = LogToEntity.getLog(s);
        if (null != log){
            String rowKey = log.getUserId() + "";
            HbaseClient.increamColumn("history",rowKey,"p",String.valueOf(log.getProductId()));
        }
        return "";
    }
}
