package com.demo.task;

import com.demo.map.GetLogFunction;
import com.demo.map.UserHistoryWithInterestMapFunction;
import com.demo.util.KafkaSources;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class UserInterestTask {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> dataStream = KafkaSources.con(env, "interest");
        dataStream.map(new GetLogFunction()).keyBy("userId").map(new UserHistoryWithInterestMapFunction());

        env.execute("User Product History");
    }
}
