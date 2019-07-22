package com.demo.window;

import com.demo.domain.TopProductEntity;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class WindowResultFunction
        implements WindowFunction<Long, TopProductEntity, Tuple, TimeWindow> {
    @Override
    public void apply(Tuple key, TimeWindow window, Iterable<Long> aggregateResult, Collector<TopProductEntity> collector) throws Exception {
		int itemId = key.getField(0);
		Long count = aggregateResult.iterator().next();
        collector.collect(TopProductEntity.of(itemId,window.getEnd(),count));
    }
}
