package com.example.config;

import com.example.kafka.DefaultProducer;
import com.example.kafka.WebsocketConsumer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(KafkaConfig.WebsocketConsumerProperties.class)
@Slf4j
public class KafkaConfig {

    /** 广播消息主题 */
    public static final String BROADCAST_TOPIC = "broadcast";
    /** 点对点消息主题 */
    public static final String CHAT_TOPIC = "chat";
    @Autowired
    private KafkaProperties kafkaProperties;
    @Autowired
    private KafkaConfig.WebsocketConsumerProperties websocketConsumerProperties;

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> websocketListenerContainerFactory() {
        Map<String, Object> properties = kafkaProperties.buildConsumerProperties();
        properties.putAll(websocketConsumerProperties.getProperties());
        ConsumerFactory<String, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(properties);
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(kafkaProperties.getListener().getConcurrency());
        return factory;
    }

    @Bean
    public DefaultProducer defaultProducer() {
        return new DefaultProducer();
    }

    @Bean
    public WebsocketConsumer websocketConsumer() {
        return new WebsocketConsumer();
    }

    @Data
    @ConfigurationProperties(prefix = "kafka.consumer.websocket")
    public class WebsocketConsumerProperties {
        private final Map<String, String> properties = new HashMap<>();
    }

}
