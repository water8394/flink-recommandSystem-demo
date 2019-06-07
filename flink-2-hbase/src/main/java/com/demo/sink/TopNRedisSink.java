package com.demo.sink;

import com.demo.entity.TopProductEntity;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

public class TopNRedisSink implements RedisMapper<TopProductEntity> {

    private String rankName = "000";

    @Override
    public RedisCommandDescription getCommandDescription() {
        return new RedisCommandDescription(RedisCommand.ZADD, rankName);
    }

    @Override
    public String getKeyFromData(TopProductEntity topProductEntity) {

        return String.valueOf(topProductEntity.getProductId());
    }

    @Override
    public String getValueFromData(TopProductEntity topProductEntity) {
        return String.valueOf(topProductEntity.getActionTimes());
    }
}
