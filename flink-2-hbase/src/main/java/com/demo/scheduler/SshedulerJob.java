package com.demo.scheduler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SshedulerJob {

    /**
     * 每12小时定时调度一次 基于两个推荐策略的 产品评分计算
     * 策略1 ：协同过滤
     *
     *        数据写入Hbase表  px
     *
     * 策略2 ： 基于产品标签 计算产品的余弦相似度
     *
     *        数据写入Hbase表 ps
     *
     * @param args
     */
    public static void main(String[] args) {
        ScheduledExecutorService pool = new ScheduledThreadPoolExecutor(2);
        // 每12小时调度一次
        pool.scheduleAtFixedRate(new Task(), 0, 12, TimeUnit.HOURS);

    }

    private static class Task implements Runnable{
        ItemCfCoeff item = new ItemCfCoeff();
        ProductCoeff prod = new ProductCoeff();
        @Override
        public void run() {
            try {
                item.getSingelItemCfCoeff("1",new String[]{"2","3"});
                prod.getSingelProductCoeff("1",new String[]{"2","3"});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
