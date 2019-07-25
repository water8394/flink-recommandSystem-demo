package com.demo.service.impl;

import com.demo.service.KafkaService;
import com.demo.util.Property;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Properties;

@Service("kafkaService")
public class kafkaServiceImpl implements KafkaService {

    private String TOPIC = "log";

    @Override
    public void send(String key, String value) {

        Properties kafkaProperties = Property.getKafkaProperties("log");

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(kafkaProperties);

        ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC, key, value);

        try {
            producer.send(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String makeLog(String userId, String productId, String action) {

        StringBuilder sb = new StringBuilder();
        sb.append(userId);
        sb.append(",");
        sb.append(productId);
        sb.append(",");
        sb.append(getSecondTimestamp(new Date()));
        sb.append(",");
        sb.append(action);
        sb.append(",");
        return sb.toString();
    }

    public static String getSecondTimestamp(Date date) {
        if (null == date) {
            return "";
        }
        String timestamp = String.valueOf(date.getTime());
        int length = timestamp.length();
        if (length > 3) {
            return timestamp.substring(0, length - 3);
        } else {
            return "";
        }
    }
}
