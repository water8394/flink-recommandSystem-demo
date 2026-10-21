package com.demo.task;

import com.demo.map.UserPortraitMapFunction;
import com.demo.util.KafkaSources;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 用户画像 -> Hbase
 *
 * @author XINZE
 */
public class UserPortraitTask {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> dataStream = KafkaSources.con(env, "userPortrait");
        dataStream.map(new UserPortraitMapFunction());
        env.execute("User Portrait");
    }
}
