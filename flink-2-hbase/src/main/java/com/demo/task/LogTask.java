package com.demo.task;

import com.demo.map.LogMapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import java.util.Properties;

public class LogTask {

    public static void main(String[] args) {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        /**
         * 这里主要配置KafkaConsumerConfig需要的属性，如：
         * --bootstrap.servers xinze:9092 --topic conLog --group.id log-group
         */

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "xinze:9092");
        properties.setProperty("zookeeper.connect", "xinze:2181");
        properties.setProperty("group.id", "test");
        DataStreamSource<String> dataStream = env.addSource(new FlinkKafkaConsumer<String>("con", new SimpleStringSchema(), properties));
        dataStream.map(new LogMapFunction());


    }

}
