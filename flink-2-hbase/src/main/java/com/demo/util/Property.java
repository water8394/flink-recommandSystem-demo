package com.demo.util;

import java.util.Properties;

public class Property {

    public static Properties getKafkaProperties(String groupId){
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "xinze:9092");
        properties.setProperty("zookeeper.connect", "xinze:2181");
        properties.setProperty("group.id", groupId);
        return properties;
    }
 }
