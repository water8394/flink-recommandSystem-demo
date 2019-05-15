package com.demo.map;

import com.demo.client.HbaseClient;
import com.demo.entity.LogEntity;
import org.apache.flink.api.common.functions.MapFunction;

public class LogMapFunction implements MapFunction<String, LogEntity> {

    public LogEntity map(String s) throws Exception {
        String[] values = s.split(",");
        if (values.length < 2) {
            return null;
        }
        LogEntity log = new LogEntity();
        log.setUserId(Integer.parseInt(values[0]));
        log.setProductId(Integer.parseInt(values[1]));
        log.setTime(values[2]);
        log.setAction(values[3]);

        String rowKey = values[0] + "#" + values[1]+ "#"+ values[2];
        HbaseClient.putData("con",rowKey,"log","userid",values[0]);
        HbaseClient.putData("con",rowKey,"log","productid",values[1]);
        HbaseClient.putData("con",rowKey,"log","time",values[2]);
        HbaseClient.putData("con",rowKey,"log","action",values[3]);
        return log;
    }
}
