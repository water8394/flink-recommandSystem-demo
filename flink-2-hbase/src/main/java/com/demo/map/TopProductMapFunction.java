package com.demo.map;

import com.demo.entity.LogEntity;
import com.demo.entity.TopProductEntity;
import com.demo.util.LogToEntity;
import org.apache.flink.api.common.functions.MapFunction;

/**
 * @author XINZE
 */
public class TopProductMapFunction implements MapFunction<String, TopProductEntity> {
    @Override
    public TopProductEntity map(String s) throws Exception {
        LogEntity log = LogToEntity.getLog(s);
        TopProductEntity topProductEntity = new TopProductEntity();
        if (null != log){
            topProductEntity.setProductId(log.getProductId());
            topProductEntity.setActionTimes(1);
        }
        return topProductEntity;
    }
}
