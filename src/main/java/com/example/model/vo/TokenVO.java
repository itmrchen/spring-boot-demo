package com.example.model.vo;

import com.example.model.po.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

// 令牌
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenVO implements Serializable {

    private static final long serialVersionUID = 1L;
    // 令牌
    private String token;
    // 登录时间戳
    private Long loginTime;
    // 当前用户资源
    private Set<String> authorities;
    // 用户信息
    private User user;

}
