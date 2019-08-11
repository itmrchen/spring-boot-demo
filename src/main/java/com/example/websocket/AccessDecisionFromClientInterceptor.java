package com.example.websocket;

import com.alibaba.fastjson.JSON;
import com.example.exception.ProjectException;
import com.example.model.po.Resource;
import com.example.model.vo.ChatMessageVO;
import com.example.service.IResourceService;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageChannel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

// websocket消息授权决策
@Slf4j
public class AccessDecisionFromClientInterceptor implements FromClientInterceptor {

    @Value("${resource.type.websocket}")
    private String websocket;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private MessageResourceMatcher matcher;

    @Override
    public boolean preHandle(WebSocketSession session, Authentication authentication, MessageFromClient message, MessageChannel outputChannel, StompSubProtocolHandler handler) {
        String type = message.getType();
        // 不拦截无主题类型
        if ("CONNECT".equals(type) || "CONNECT_ACK".equals(type) || "HEARTBEAT".equals(type) || "UNSUBSCRIBE".equals(type)
                || "DISCONNECT".equals(type) || "DISCONNECT_ACK".equals(type) || "OTHER".equals(type)) {
            return true;
        }
        // SEND和SUBSCRIBE消息
        if (authentication==null) {
            notifyUser(session, "未登录！");
            return false;
        }
        Resource resource = matchResource(message);
        if (resource==null) {
            // 禁止访问未定义的资源
            notifyUser(session, "禁止访问！");
            return false;
        }
        // 当前用户资源
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // 匹配资源
        GrantedAuthority authority = matchAuthority(resource, authorities);
        if (authority==null) {
            notifyUser(session, "权限不足！");
            return false;
        }
        return true;
    }

    /*
     * 遍历资源，根据message的类型和destination匹配资源，
     * 获取到当前资源resource，
     * 如果resource==null，禁止访问
     * 注：这里和http请求授权策略不一样，http请求如果访问controller中没有定义的url会返回404，
     * 所以在http请求授权策略中当resource==null时默认为不需要权限
     * 而websocket如果不做限制可以发送任意主题的消息，所以为了安全，禁止用户发送资源定义以外的主题
     */
    private Resource matchResource(MessageFromClient message) {
        Resource resource = null;
        // 查询所有websocket类型的资源
        List<Resource> resources = resourceService.getByType(websocket);
        for (Resource r : resources) {
            if (matcher.matches(r, message)) {
                resource = r;
            }
        }
        return resource;
    }

    /*
     * 如果authorities（当前用户资源）不为空 !Collections.isEmpty(authorities)
     * resource和authorities进行匹配，
     * 匹配上则放行 return true，
     * 匹配不上则调用session.sendMessage()，将错误信息发送给客户端 return false
     */
    private GrantedAuthority matchAuthority(Resource resource, Collection<? extends GrantedAuthority> authorities) {
        if (Collections.isEmpty(authorities)) {
            return null;
        }
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(String.valueOf(resource.getId()))) {
                // 如果匹配到表示用户有权限
                return authority;
            }
        }
        return null;
    }

    // 通知用户
    private void notifyUser(WebSocketSession session, String message) {
        ChatMessageVO chatMessageVO = new ChatMessageVO();
        chatMessageVO.setSendTime(System.currentTimeMillis());
        chatMessageVO.setContent(message);
        ConcurrentWebSocketSessionDecorator decorator = new ConcurrentWebSocketSessionDecorator(session, -1, -1);
        try {
            decorator.sendMessage(new TextMessage(" "+JSON.toJSONString(chatMessageVO)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ProjectException("发送消息出错！", e);
        }
    }

}
