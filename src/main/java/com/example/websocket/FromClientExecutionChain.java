package com.example.websocket;

import org.springframework.messaging.MessageChannel;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;

import java.util.List;

public class FromClientExecutionChain {

    private List<FromClientInterceptor> interceptors;

    private int interceptorIndex = -1;

    public FromClientExecutionChain(List<FromClientInterceptor> interceptors) {
        Assert.notNull(interceptors, "interceptors不能为null");
        this.interceptors = interceptors;
    }

    public boolean applyPreHandle(WebSocketSession session, Authentication authentication, MessageFromClient message, MessageChannel outputChannel, StompSubProtocolHandler handler) {
        if (!ObjectUtils.isEmpty(interceptors)) {
            for (int i=0; i<interceptors.size(); i++) {
                FromClientInterceptor interceptor = interceptors.get(i);
                if (!interceptor.preHandle(session, authentication, message, outputChannel, handler)) {
                    applyPostHandle(session, authentication, message, outputChannel, handler);
                    return false;
                }
                this.interceptorIndex = i;
            }
        }
        return true;
    }

    public void applyPostHandle(WebSocketSession session, Authentication authentication, MessageFromClient message, MessageChannel outputChannel, StompSubProtocolHandler handler) {
        if (!ObjectUtils.isEmpty(interceptors)) {
            for (int i = this.interceptorIndex; i >= 0; i--) {
                FromClientInterceptor interceptor = interceptors.get(i);
                interceptor.postHandle(session, authentication, message, outputChannel, handler);
            }
        }
    }

}
