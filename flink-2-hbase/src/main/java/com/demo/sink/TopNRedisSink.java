package com.demo.sink;

import com.demo.domain.TopProductEntity;
import com.demo.util.Property;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import redis.clients.jedis.Jedis;

/**
 * Writes a hot-product rank with Redis {@code SET rankName productId}.
 * Host only: port 6379 and database 0, which is what the previous
 * {@code FlinkJedisPoolConfig} used when port and database were left commented out.
 */
public class TopNRedisSink extends RichSinkFunction<TopProductEntity> {

    private transient Jedis jedis;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        // Jedis 5 treats a single string as a URI, so "localhost" is rejected.
        // Same host and port RedisClient uses.
        jedis = new Jedis(Property.getStrValue("redis.host"), Property.getIntValue("redis.port"));
    }

    @Override
    public void invoke(TopProductEntity value, Context context) {
        jedis.set(String.valueOf(value.getRankName()), String.valueOf(value.getProductId()));
    }

    @Override
    public void close() {
        if (jedis != null) {
            jedis.close();
        }
    }
}
