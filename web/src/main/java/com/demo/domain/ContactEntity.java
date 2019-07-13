package com.demo.domain;

public class ContactEntity {

    private int id;
    private String picUrl;
    private String itemName;
    private String subName;
    private double martPrice;
    private String brandName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public double getMartPrice() {
        return martPrice;
    }

    public void setMartPrice(double martPrice) {
        this.martPrice = martPrice;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    @Override
    public String toString() {
        return "ContactEntity{" +
                "id=" + id +
                ", picUrl='" + picUrl + '\'' +
                ", itemName='" + itemName + '\'' +
                ", subName='" + subName + '\'' +
                ", martPrice=" + martPrice +
                ", brandName='" + brandName + '\'' +
                '}';
    }
}
