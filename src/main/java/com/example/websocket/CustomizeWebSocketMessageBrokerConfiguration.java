package com.example.websocket;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.DelegatingWebSocketMessageBrokerConfiguration;
import org.springframework.web.socket.config.annotation.WebMvcStompEndpointRegistry;

/**
 * 代替{@link DelegatingWebSocketMessageBrokerConfiguration}，
 * 使用自定义的{@link CustomizeWebMvcStompEndpointRegistry}
 * 代替默认的{@link WebMvcStompEndpointRegistry}
 */
@Configuration
public class CustomizeWebSocketMessageBrokerConfiguration extends DelegatingWebSocketMessageBrokerConfiguration {

    @Bean
    @Override
    public HandlerMapping stompWebSocketHandlerMapping() {
        WebSocketHandler handler = decorateWebSocketHandler(subProtocolWebSocketHandler());
        // 使用 CustomizeWebMvcStompEndpointRegistry 代替 WebMvcStompEndpointRegistry
        CustomizeWebMvcStompEndpointRegistry registry = new CustomizeWebMvcStompEndpointRegistry(
                handler, getTransportRegistration(), messageBrokerTaskScheduler());
        ApplicationContext applicationContext = getApplicationContext();
        if (applicationContext != null) {
            registry.setApplicationContext(applicationContext);
        }
        registerStompEndpoints(registry);
        return registry.getHandlerMapping();
    }

}
