package com.demo.sink;

import com.demo.domain.TopProductEntity;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

public class TopNRedisSink implements RedisMapper<TopProductEntity> {


    @Override
    public RedisCommandDescription getCommandDescription() {
        return new RedisCommandDescription(RedisCommand.SET, null);
    }

    @Override
    public String getKeyFromData(TopProductEntity s) {
        return String.valueOf(s.getRankName());
    }

    @Override
    public String getValueFromData(TopProductEntity s) {
        return String.valueOf(s.getProductId());
    }
}
