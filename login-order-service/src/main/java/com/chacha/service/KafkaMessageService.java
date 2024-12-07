package com.chacha.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service // 카프카 클러스터로 전송
public class KafkaMessageService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaMessageService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLoginEvent(String event) {
        try {
            kafkaTemplate.send("login-event", event);
            System.out.println("Login event sent to Kafka: \n" + event);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to send login event to Kafka.");
        }
    }

    public void sendOrderEvent(String event) {
        try {
            kafkaTemplate.send("order-event", event);
            System.out.println("Order event sent to Kafka: \n" + event);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to send order event to Kafka.");
        }
    }
}