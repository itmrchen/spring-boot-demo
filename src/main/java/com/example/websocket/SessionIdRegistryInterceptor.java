package com.example.websocket;

import com.example.model.vo.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;

public class SessionIdRegistryInterceptor implements ToClientInterceptor {

    @Autowired
    private SessionIdRegistry sessionIdRegistry;

    @Override
    public void postHandle(WebSocketSession session, Authentication authentication, StompHeaderAccessor accessor, Object payload, StompSubProtocolHandler handler) {
        if (authentication==null) {
            return;
        }
        SimpMessageType type = accessor.getMessageType();
        if (SimpMessageType.CONNECT_ACK.equals(type)) {
            // 记录sessionId
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Integer userId = userDetails.getUser().getId();
            String sessionId = session.getId();
            sessionIdRegistry.registerSessionId(userId, sessionId);
        }
    }

}
