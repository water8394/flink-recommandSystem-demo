package com.demo.map;

import com.demo.client.HbaseClient;
import com.demo.domain.LogEntity;
import com.demo.util.LogToEntity;
import org.apache.flink.api.common.functions.MapFunction;

/**
 * @author XINZE
 */
public class LogMapFunction implements MapFunction<String, LogEntity> {

    @Override
    public LogEntity map(String s) throws Exception {
        System.out.println(s);
        LogEntity log = LogToEntity.getLog(s);
        if (null != log){
            String rowKey = log.getUserId() + "_" + log.getProductId()+ "_"+ log.getTime();
            HbaseClient.putData("con",rowKey,"log","userid",String.valueOf(log.getUserId()));
            HbaseClient.putData("con",rowKey,"log","productid",String.valueOf(log.getProductId()));
            HbaseClient.putData("con",rowKey,"log","time",log.getTime().toString());
            HbaseClient.putData("con",rowKey,"log","action",log.getAction());
        }
        return log;
    }
}
