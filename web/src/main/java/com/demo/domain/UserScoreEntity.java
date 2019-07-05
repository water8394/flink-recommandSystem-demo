package com.demo.domain;

import java.util.Arrays;

/**
 * @author XINZE
 */
public class UserScoreEntity {
    private String userId;
    private Double[] color;
    private Double[] country;
    private Double[] style;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double[] getColor() {
        return color;
    }

    public void setColor(Double[] color) {
        this.color = color;
    }

    public Double[] getCountry() {
        return country;
    }

    public void setCountry(Double[] country) {
        this.country = country;
    }

    public Double[] getStyle() {
        return style;
    }

    public void setStyle(Double[] style) {
        this.style = style;
    }

    @Override
    public String toString() {
        return "UserScoreEntity{" +
                "userId='" + userId + '\'' +
                ", color=" + Arrays.toString(color) +
                ", country=" + Arrays.toString(country) +
                ", style=" + Arrays.toString(style) +
                '}';
    }
}
