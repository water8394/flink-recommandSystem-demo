package com.demo.sink;

import com.demo.entity.RankProductEntity;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

public class TopNRedisSink implements RedisMapper<RankProductEntity> {

    @Override
    public RedisCommandDescription getCommandDescription() {
        return new RedisCommandDescription(RedisCommand.SET, "flink");
    }

    @Override
    public String getKeyFromData(RankProductEntity rankProductEntity) {
        return String.valueOf(rankProductEntity.getId());
    }

    @Override
    public String getValueFromData(RankProductEntity rankProductEntity) {
        return String.valueOf(rankProductEntity.getProductId());
    }
}
