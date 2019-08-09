package com.demo.service.impl;

import com.demo.service.KafkaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Date;

import static org.apache.hadoop.hbase.TestChoreService.log;

@Service
public class KafkaServiceImpl implements KafkaService {

	private Logger log = LoggerFactory.getLogger(KafkaService.class);

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

    private String TOPIC = "con";

    @Override
    public void send(String key, String value) {
		ListenableFuture<SendResult<String, String>> send = kafkaTemplate.send(TOPIC, key, value);

		send.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

			@Override
			public void onFailure(Throwable throwable) {
				log.error("kafka send msg err, ex = {}, topic = {}, data = {}", throwable, TOPIC, value);
			}

			@Override
			public void onSuccess(SendResult<String, String> integerStringSendResult) {
				log.info("kafka send msg success, topic = {}, data = {}", TOPIC, value);
			}
		});
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
