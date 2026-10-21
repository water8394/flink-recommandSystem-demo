package com.demo.task;

import com.demo.map.ProductPortraitMapFunction;
import com.demo.util.KafkaSources;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 产品画像 -> Hbase
 *
 * @author XINZE
 */
public class ProductProtaritTask {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> dataStream = KafkaSources.con(env, "ProductPortrait");
        dataStream.map(new ProductPortraitMapFunction());
        env.execute("Product Portrait");

    }
}
