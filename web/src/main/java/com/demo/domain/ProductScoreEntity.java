package com.demo.domain;

public class ProductScoreEntity {
    private ProductEntity product;
    private double score;
    private int rank;

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "ProductScoreEntity{" +
                "product=" + product +
                ", score=" + score +
                ", rank=" + rank +
                '}';
    }
}
