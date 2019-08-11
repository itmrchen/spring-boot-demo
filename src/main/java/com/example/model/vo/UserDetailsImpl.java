package com.example.model.vo;

import com.example.model.po.Resource;
import com.example.model.po.Role;
import com.example.model.po.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.jsonwebtoken.lang.Collections;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;
    private User user;
    private String token;
    /** 登陆时间戳 */
    private Long loginTime;
    /** 过期时间戳 */
    private Long expireTime;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        List<Role> roles = user.getRoles();
        if (!Collections.isEmpty(roles)) {
            for (Role role: roles) {
                List<Resource> resources = role.getResources();
                if (!Collections.isEmpty(resources)) {
                    for (Resource resource: resources) {
                        authorities.add(new SimpleGrantedAuthority(String.valueOf(resource.getId())));
                    }
                }
            }
        }
        return authorities;
    }
    @Override
    @JsonIgnore
    public String getPassword() {
        return user.getPassword();
    }
    @Override
    @JsonIgnore
    public String getUsername() {
        return user.getUsername();
    }
    // 账户是否未过期
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }
    // 账户是否未锁定
    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }
    // 密码是否未过期
    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }
    // 账户是否激活
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

}
