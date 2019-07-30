package com.demo.util;

import com.demo.domain.LogEntity;

/**
 * @author XINZE
 */
public class LogToEntity {

    public static LogEntity getLog(String s){
        System.out.println(s);
        String[] values = s.split(",");
        if (values.length < 2) {
            System.out.println("Message is not correct");
            return null;
        }
        LogEntity log = new LogEntity();
        log.setUserId(Integer.parseInt(values[0]));
        log.setProductId(Integer.parseInt(values[1]));
        log.setTime(Long.parseLong(values[2]));
        log.setAction(values[3]);

        return log;
    }
}
