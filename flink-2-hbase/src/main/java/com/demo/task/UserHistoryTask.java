package com.demo.task;

import com.demo.map.UserHistoryMapFunction;
import com.demo.util.KafkaSources;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class UserHistoryTask {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> dataStream = KafkaSources.con(env, "history");
        dataStream.map(new UserHistoryMapFunction());

        env.execute("User Product History");
    }
}
