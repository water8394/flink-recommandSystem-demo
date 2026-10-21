package com.demo.util;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import java.util.Properties;

/**
 * Kafka source for the {@code con} topic.
 * Group offsets are resumed when present, otherwise the consumer starts at the latest offset,
 * matching {@code FlinkKafkaConsumer}'s default startup mode.
 */
public final class KafkaSources {

    private KafkaSources() {
    }

    public static DataStreamSource<String> con(StreamExecutionEnvironment env, String groupId) {
        Properties properties = Property.getKafkaProperties(groupId);
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(properties.getProperty("bootstrap.servers"))
                .setTopics("con")
                .setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST))
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        return env.fromSource(source, WatermarkStrategy.noWatermarks(), "kafka-" + groupId);
    }
}
