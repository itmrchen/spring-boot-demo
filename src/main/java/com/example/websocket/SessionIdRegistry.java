package com.example.websocket;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.jsonwebtoken.lang.Assert;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class SessionIdRegistry {

    private final Map<Integer, Set<String>> userSessionIds = Maps.newConcurrentMap();

    // 根据用户id获取sessionId集合
    public Set<String> getSessionIds(Integer userId) {
        Set<String> sessionIds = this.userSessionIds.get(userId);
        return sessionIds!=null? sessionIds: Collections.emptySet();
    }

    public Map<Integer, Set<String>> getAllSessionIds() {
        return this.userSessionIds;
    }

    // 登记sessionId
    public void registerSessionId(Integer userId, String sessionId) {
        Assert.notNull(userId, "userId不能为null");
        Assert.notNull(sessionId, "sessionId不能为null");
        Set<String> sessionIds = userSessionIds.get(userId);
        if (sessionIds==null) {
            sessionIds = Sets.newCopyOnWriteArraySet();
            this.userSessionIds.put(userId, sessionIds);
        }
        sessionIds.add(sessionId);
    }

    // 移除sessionId
    public void unRegisterSessionId(Integer userId, String sessionId) {
        Assert.notNull(userId, "userId不能为null");
        Assert.notNull(sessionId, "sessionId不能为null");
        Set<String> sessionIds = userSessionIds.get(userId);
        if (sessionIds!=null && sessionIds.remove(sessionId) && sessionIds.isEmpty()) {
            this.userSessionIds.remove(userId);
        }
    }

}
