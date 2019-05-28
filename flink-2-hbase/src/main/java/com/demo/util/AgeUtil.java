package com.demo.util;

public class AgeUtil {

    public static String getAgeType(String age){
        int number = Integer.valueOf(age);
        if (10 <= number && number < 20){
            return "10s";}
        else if (20 <= number && number < 30){
            return "20s";}
        else if (30 <= number && number < 40){
            return "30s";}
        else if (40 <= number && number < 50){
            return "40s";}
        else if (50 <= number && number < 60){
            return "50s";}
        else {
            return "0s";
        }
    }
}
