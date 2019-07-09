package com.demo.domain;

public class ProductPortraitEntity {

    private int man;
    private int woman;

    private int age_10;
    private int age_20;
    private int age_30;
    private int age_40;
    private int age_50;
    private int age_60;


    public int getMan() {
        return man;
    }

    public void setMan(int man) {
        this.man = man;
    }

    public int getWoman() {
        return woman;
    }

    public void setWoman(int woman) {
        this.woman = woman;
    }

    public int getAge_10() {
        return age_10;
    }

    public void setAge_10(int age_10) {
        this.age_10 = age_10;
    }

    public int getAge_20() {
        return age_20;
    }

    public void setAge_20(int age_20) {
        this.age_20 = age_20;
    }

    public int getAge_30() {
        return age_30;
    }

    public void setAge_30(int age_30) {
        this.age_30 = age_30;
    }

    public int getAge_40() {
        return age_40;
    }

    public void setAge_40(int age_40) {
        this.age_40 = age_40;
    }

    public int getAge_50() {
        return age_50;
    }

    public void setAge_50(int age_50) {
        this.age_50 = age_50;
    }

    public int getAge_60() {
        return age_60;
    }

    public void setAge_60(int age_60) {
        this.age_60 = age_60;
    }


    public int getTotal(){
        int ret = 0;
        ret += (man*man) + (woman*woman) + (age_10*age_10) + (age_20*age_20) + (age_30*age_30) + (age_40*age_40) +
                (age_50*age_50) + (age_60*age_60);
        return ret;
    }
}
