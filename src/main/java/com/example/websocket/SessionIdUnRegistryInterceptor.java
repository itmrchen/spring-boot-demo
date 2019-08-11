package com.example.websocket;

import com.example.model.vo.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;

public class SessionIdUnRegistryInterceptor implements FromClientInterceptor {

    @Autowired
    private SessionIdRegistry sessionIdRegistry;

    @Override
    public void postHandle(WebSocketSession session, Authentication authentication, MessageFromClient message, MessageChannel outputChannel, StompSubProtocolHandler handler) {
        if (authentication==null) {
            return;
        }
        if ("DISCONNECT".equals(message.getType())) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Integer userId = userDetails.getUser().getId();
            String sessionId = session.getId();
            sessionIdRegistry.unRegisterSessionId(userId, sessionId);
        }
    }

}
