package com.demo.entity;

/**
 * @author XINZE
 */
public class TopProductEntity {

    private int productId;
    private int actionTimes;
    private long timeStamp;
    private String rankName;

    public String getRankName() {
        return rankName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getActionTimes() {
        return actionTimes;
    }

    public void setActionTimes(int actionTimes) {
        this.actionTimes = actionTimes;
    }
}
