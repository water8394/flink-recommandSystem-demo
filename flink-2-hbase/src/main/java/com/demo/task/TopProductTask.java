package com.demo.task;

import com.demo.entity.TopProductEntity;
import com.demo.map.TopProductMapFunction;
import com.demo.reduce.TopPruductReduceFunction;
import com.demo.sink.TopNRedisSink;
import com.demo.top.WindowFunction;
import com.demo.util.Property;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
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
        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder().setHost("192.168.0.100").build();
        Properties properties = Property.getKafkaProperties("topProuct");
        DataStreamSource<String> dataStream = env.addSource(new FlinkKafkaConsumer<String>("con", new SimpleStringSchema(), properties));
        DataStream<TopProductEntity> topProduct = dataStream.map(new TopProductMapFunction())
                .keyBy("productId").window(SlidingProcessingTimeWindows.of(Time.seconds(600),Time.seconds(5)))
                .reduce(new TopPruductReduceFunction()).windowAll(TumblingProcessingTimeWindows.of(Time.seconds(5))).
                process(new WindowFunction());
        topProduct.addSink(new RedisSink<>(conf,new TopNRedisSink()));

        env.execute("Top N ");
    }
}
