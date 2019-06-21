package com.demo.scheduler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SshedulerJob {

    /**
     * 每12小时定时调度一次 基于两个推荐策略的 产品评分计算
     * 策略1 ：协同过滤
     *           abs( i ∩ j)
     *      w = ——————————————
     *           sqrt(i || j)
     *
     *
     * 策略2 ： 基于产品标签 计算产品的余弦相似度
     *
     *     w = sqrt( pow((tag{i,a} - tag{j,a}),2)  + pow((tag{i,b} - tag{j,b}),2) )
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
