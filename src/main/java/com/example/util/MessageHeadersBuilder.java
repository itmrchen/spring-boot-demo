package com.example.util;

import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;

public class MessageHeadersBuilder {

    private SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);

    public MessageHeadersBuilder sessionId(String sessionId) {
        this.headerAccessor.setSessionId(sessionId);
        return this;
    }

    public MessageHeadersBuilder leaveMutable(boolean leaveMutable) {
        this.headerAccessor.setLeaveMutable(leaveMutable);
        return this;
    }

    public MessageHeadersBuilder destination(String destination) {
        this.headerAccessor.setDestination(destination);
        return this;
    }

    public MessageHeaders build() {
        return this.headerAccessor.getMessageHeaders();
    }

}
