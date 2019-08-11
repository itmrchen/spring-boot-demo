package com.example.websocket;

import com.example.model.po.Resource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.Objects;

public class MessageResourceMatcher {

    private PathMatcher matcher = new AntPathMatcher();

    public boolean matches(Resource resource, MessageFromClient message) {
        if (!Objects.equals(resource.getResourceMethod(), message.getType())) {
            return false;
        }
        String pattern = resource.getResourcePattern();
        String destination = message.getDestination();
        if (pattern==null) {
            pattern = "";
        }
        if (destination==null) {
            destination = "";
        }
        return matcher.match(pattern, destination);
    }

}
