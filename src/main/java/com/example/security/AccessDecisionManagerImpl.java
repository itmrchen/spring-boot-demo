package com.example.security;

import com.example.service.ITokenService;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

// 自定义授权决策
@Component
public class AccessDecisionManagerImpl implements AccessDecisionManager {

    @Autowired
    private ITokenService tokenService;

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        // 当前用户资源
        Collection<? extends GrantedAuthority> authorities;
        if (authentication instanceof AnonymousAuthenticationToken) {
            // 游客资源
            authorities = tokenService.getGuest().getAuthorities();
        } else {
            // 当前用户资源
            authorities = authentication.getAuthorities();
        }
        if (!Collections.isEmpty(configAttributes) && !Collections.isEmpty(authorities)) {
            // 需求资源id
            String resourceId = ((List<ConfigAttribute>) configAttributes).get(0).getAttribute();
            // 遍历当前角色资源
            for (GrantedAuthority authority: authorities) {
                if (authority.getAuthority().equals(resourceId)) {
                    // 如果匹配到表示用户有权限
                    return;
                }
            }
        }
        // 没有匹配到表示权限不足
        throw new AccessDeniedException("权限不足！");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

}
