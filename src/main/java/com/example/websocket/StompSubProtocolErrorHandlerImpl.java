package com.example.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.util.LinkedList;

public class StompSubProtocolErrorHandlerImpl extends StompSubProtocolErrorHandler {

    @Override
    @SuppressWarnings("unchecked")
    protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor, byte[] errorPayload, Throwable cause, StompHeaderAccessor clientHeaderAccessor) {
        Message<byte[]> genericMessage = MessageBuilder.createMessage(errorPayload, errorHeaderAccessor.getMessageHeaders());
        LinkedMultiValueMap nativeHeaders = (LinkedMultiValueMap) genericMessage.getHeaders().get("nativeHeaders");
        if (nativeHeaders!=null) {
            LinkedList message = (LinkedList) nativeHeaders.get("message");
            if (message!=null) {
                message.clear();
//                message.add(cause.getCause().getMessage());
                message.add("请求出错！");
            }
        }
        return genericMessage;
    }

}
