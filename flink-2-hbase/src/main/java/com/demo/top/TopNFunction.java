package com.demo.top;

import com.demo.domain.RankProductEntity;
import com.demo.domain.TopProductEntity;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author XINZE
 */
public class TopNFunction extends
        ProcessAllWindowFunction<TopProductEntity, RankProductEntity,TimeWindow>{

    private int topSize = 10;
    public TopNFunction(int topSize) {
        this.topSize = topSize;
    }

    @Override
    public void process(Context context, Iterable<TopProductEntity> iterable, Collector<RankProductEntity> collector) throws Exception {
        PriorityQueue<TopProductEntity> res = new PriorityQueue<>(this.topSize, idComparator);
        for (TopProductEntity topProductEntity:
                iterable) {
            res.add(topProductEntity);
        }
        int i = 0;
        while (!res.isEmpty()){
            TopProductEntity poll = res.poll();
            System.out.println(poll.getProductId()+" :"+poll.getActionTimes());
            RankProductEntity rankProductEntity = new RankProductEntity();
            rankProductEntity.setId(String.valueOf(i++));
            rankProductEntity.setProductId(String.valueOf(poll.getProductId()));
            collector.collect(rankProductEntity);
        }
    }


    private static Comparator<TopProductEntity> idComparator = new Comparator<TopProductEntity>(){

        @Override
        public int compare(TopProductEntity c1, TopProductEntity c2) {
            return c1.getProductId() - c2.getProductId();
        }
    };

}
