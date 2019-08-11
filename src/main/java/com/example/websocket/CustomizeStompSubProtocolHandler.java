package com.example.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * 代替{@link StompSubProtocolHandler}
 * 增加了对拦截器的支持
 */
public class CustomizeStompSubProtocolHandler extends StompSubProtocolHandler {

    private List<FromClientInterceptor> fromClientInterceptors = new ArrayList<>();

    private List<ToClientInterceptor> toClientInterceptors = new ArrayList<>();

    @Override
    public void handleMessageFromClient(WebSocketSession session, WebSocketMessage<?> webSocketMessage, MessageChannel outputChannel) {
        FromClientExecutionChain chain = new FromClientExecutionChain(fromClientInterceptors);
        Authentication authentication = (Authentication) session.getPrincipal();
        MessageFromClient message = getMessageFromClient(webSocketMessage);
        if (chain.applyPreHandle(session, authentication, message, outputChannel, this)) {
            super.handleMessageFromClient(session, webSocketMessage, outputChannel);
            chain.applyPostHandle(session, authentication, message, outputChannel, this);
        }
    }

    @Override
    public void handleMessageToClient(WebSocketSession session, Message<?> message) {
        ToClientExecutionChain chain = new ToClientExecutionChain(toClientInterceptors);
        Authentication authentication = (Authentication) session.getPrincipal();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        Object payload = message.getPayload();
        if (chain.applyPreHandle(session, authentication, accessor, payload, this)) {
            super.handleMessageToClient(session, message);
            chain.applyPostHandle(session, authentication, accessor, payload, this);
        }
    }

    public void addFromClientInterceptor(FromClientInterceptor interceptor) {
        Assert.notNull(interceptor, "interceptor不能为null");
        this.fromClientInterceptors.add(interceptor);
    }

    public void addToClientInterceptor(ToClientInterceptor interceptor) {
        Assert.notNull(interceptor, "interceptor不能为null");
        this.toClientInterceptors.add(interceptor);
    }

    private MessageFromClient getMessageFromClient(WebSocketMessage<?> webSocketMessage) {
        if (webSocketMessage instanceof TextMessage) {
            MessageFromClient message = new MessageFromClient();
            String payload = ((TextMessage) webSocketMessage).getPayload();
            String[] arr = payload.split("\n");
            Queue<String> queue = new LinkedTransferQueue<>();
            for (String str: arr) {
                String strTrim = str.trim();
                if (StringUtils.isEmpty(strTrim)) {
                    continue;
                }
                queue.offer(strTrim);
            }
            String type = queue.poll();
            message.setType(type);
            int last = 0;
            if ("SEND".equals(type)) {
                last = 1;
            }
            while (queue.size()>last) {
                String param = queue.poll();
                if (param.startsWith("id:")) {
                    message.setSubId(param.split(":")[1].trim());
                } else if (param.startsWith("destination:")) {
                    message.setDestination(param.split(":")[1].trim());
                } else if (param.startsWith("content-length:")) {
                    message.setContentLength(Integer.valueOf(param.split(":")[1].trim()));
                }
            }
            String content = queue.poll();
            if (!StringUtils.isEmpty(content)) {
                if (content.startsWith("destination:") && StringUtils.isEmpty(message.getDestination())) {
                    message.setDestination(content.split(":")[1].trim());
                } else {
                    message.setContent(content.trim());
                }
            }
            return message;
        }
        return null;
    }

}
