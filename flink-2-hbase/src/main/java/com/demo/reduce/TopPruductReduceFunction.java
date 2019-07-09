package com.demo.reduce;

import com.demo.domain.TopProductEntity;
import org.apache.flink.api.common.functions.ReduceFunction;

/**
 * @author XINZE
 */
public class TopPruductReduceFunction implements ReduceFunction<TopProductEntity> {
    @Override
    public TopProductEntity reduce(TopProductEntity t1, TopProductEntity t2) throws Exception {

        TopProductEntity top = new TopProductEntity();
        top.setProductId(t1.getProductId());
        top.setActionTimes(t1.getActionTimes() + t2.getActionTimes());
        return top;
    }
}
