package com.example.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
public class DefaultProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    private ListenableFutureCallback<SendResult<String, Object>> defaultCallback = new ListenableFutureCallback<SendResult<String, Object>>() {
        @Override
        public void onSuccess(SendResult<String, Object> result) {
            log.info("发送消息成功，result: "+result);
        }
        @Override
        public void onFailure(Throwable ex) {
            log.error("发送消息失败，ex: "+ex);
        }
    };

    public void send(String topic, Object data) {
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, data);
        future.addCallback(defaultCallback);
    }

}
