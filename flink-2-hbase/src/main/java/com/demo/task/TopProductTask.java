package com.demo.task;

import com.demo.entity.RankProductEntity;
import com.demo.entity.TopProductEntity;
import com.demo.map.TopProductMapFunction;
import com.demo.reduce.TopPruductReduceFunction;
import com.demo.sink.TopNRedisSink;
import com.demo.top.TopNFunction;
import com.demo.util.Property;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import java.util.Properties;

/**
 * 热门商品 -> redis
 *
 * @author XINZE
 */
public class TopProductTask {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = Property.getKafkaProperties("topProuct");
        DataStreamSource<String> dataStream = env.addSource(new FlinkKafkaConsumer<String>("con", new SimpleStringSchema(), properties));
        DataStream<TopProductEntity> topProduct = dataStream.map(new TopProductMapFunction())
                .keyBy("productId").timeWindow(Time.milliseconds(10_000L)).reduce(new TopPruductReduceFunction());

        DataStream<RankProductEntity> result = topProduct.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(10))).process(new TopNFunction(10));
        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder().setHost("192.168.0.100").build();
        result.addSink(new RedisSink<>(conf,new TopNRedisSink()));
        env.execute("Top N ");
    }
}
