package com.example.config;

import com.example.websocket.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.DelegatingWebSocketMessageBrokerConfiguration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebMvcStompEndpointRegistry;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;

/**
 * 默认通过注解{@link EnableWebSocketMessageBroker}
 * 开启使用STOMP协议来传输基于代理(message broker)的消息，支持使用{@link MessageMapping}就像支持{@link RequestMapping}一样。
 * 但是注解{@link EnableWebSocketMessageBroker}会引入{@link DelegatingWebSocketMessageBrokerConfiguration}配置类，
 * 该配置类默认使用{@link WebMvcStompEndpointRegistry}，{@link WebMvcStompEndpointRegistry}的stomp协议处理器为
 * {@link StompSubProtocolHandler}，处理消息的方法：
 * @see StompSubProtocolHandler#handleMessageFromClient(WebSocketSession, WebSocketMessage, MessageChannel)
 * @see StompSubProtocolHandler#handleMessageToClient(WebSocketSession, Message)
 * 未对自定义拦截做支持，
 * 所以取消{@link EnableWebSocketMessageBroker}，使用自定义配置{@link CustomizeWebSocketMessageBrokerConfiguration}
 */
@Configuration
//@EnableWebSocketMessageBroker
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    // 异常处理器
    @Bean
    public StompSubProtocolErrorHandler stompSubProtocolErrorHandler() {
        return new StompSubProtocolErrorHandlerImpl();
    }

    // 匹配消息资源
    @Bean
    public MessageResourceMatcher messageResourceMatcher() {
        return new MessageResourceMatcher();
    }

    // 授权决策拦截器
    @Bean
    public AccessDecisionFromClientInterceptor accessDecisionFromClientInterceptor() {
        return new AccessDecisionFromClientInterceptor();
    }

    // sessionId记录
    @Bean
    public SessionIdRegistry sessionIdRegistry() {
        return new SessionIdRegistry();
    }

    // sessionId登记拦截器
    @Bean
    public SessionIdRegistryInterceptor sessionIdRegistryInterceptor() {
        return new SessionIdRegistryInterceptor();
    }

    // sessionId移除拦截器
    @Bean
    public SessionIdUnRegistryInterceptor sessionIdUnRegistryInterceptor() {
        return new SessionIdUnRegistryInterceptor();
    }

    /**
     * 只设置{@link #sameOriginDisabled}不能解决同源策略，
     * 必须在注册endpoint时设置setAllowedOrigins
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/endpoint").setAllowedOrigins("*").withSockJS();
        registry.setErrorHandler(stompSubProtocolErrorHandler());
        // 配置拦截器
        ((CustomizeWebMvcStompEndpointRegistry) registry)
                .addFromClientInterceptor(accessDecisionFromClientInterceptor())
                .addFromClientInterceptor(sessionIdUnRegistryInterceptor())
                .addToClientInterceptor(sessionIdRegistryInterceptor());
    }

    // 这里取消所有检查，统一在授权决策拦截器中处理
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.nullDestMatcher().authenticated()
                .anyMessage().permitAll();
    }

    // 关闭同源策略
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

}
