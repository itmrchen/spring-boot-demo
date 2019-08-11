package com.example.security;

import com.example.model.po.Resource;
import com.example.service.IResourceService;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

// 根据访问url获取所需权限
@Component
public class FilterInvocationSecurityMetadataSourceImpl implements FilterInvocationSecurityMetadataSource {

    @Value("${resource.type.http}")
    private String http;
    @Autowired
    private IResourceService resourceService;

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();

        // 查询所有http类型的资源
        List<Resource> resources = resourceService.getByType(http);

        Resource resource = null;
        // 按照url匹配
        if (!Collections.isEmpty(resources)) {
            for (Resource r: resources) {
                RequestMatcher requestMatcher = new AntPathRequestMatcher(r.getResourcePattern(), r.getResourceMethod());
                if (requestMatcher.matches(request)) {
                    resource = r;
                }
            }
        }

        if (resource==null) {
            return null;
        }

        return SecurityConfig.createList(String.valueOf(resource.getId()));
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

}
