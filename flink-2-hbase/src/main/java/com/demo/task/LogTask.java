package com.demo.task;

import com.demo.map.LogMapFunction;
import com.demo.util.KafkaSources;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 日志 -> Hbase
 *
 * @author XINZE
 */
public class LogTask {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> dataStream = KafkaSources.con(env, "log");
        dataStream.map(new LogMapFunction());

        env.execute("Log message receive");
    }
}
