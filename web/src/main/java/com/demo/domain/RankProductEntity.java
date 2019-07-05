package com.demo.domain;

public class RankProductEntity {

    private String id;
    private String productId;
    private Double score;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "RankProductEntity{" +
                "id='" + id + '\'' +
                ", productId='" + productId + '\'' +
                ", score=" + score +
                '}';
    }
}
