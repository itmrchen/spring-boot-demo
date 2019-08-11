package com.example.service;

import com.example.model.vo.TokenVO;
import com.example.model.vo.UserDetailsImpl;

import java.util.Set;

public interface ITokenService {

    // 游客角色名称
    String GUEST_ROLE_NAME = "ROLE_GUEST";

    // 获取token并缓存userDetails
    TokenVO saveToken(UserDetailsImpl userDetails);

    // 刷新缓存中的userDetails
    void refresh(UserDetailsImpl userDetails);

    // 根据token获取userDetails
    UserDetailsImpl getUserDetalis(String token);

    // 删除token对应的缓存中的userDetails
    void deleteToken(String token);

    // 获取游客
    UserDetailsImpl getGuest();

    // 获取资源字符串
    Set<String> getAuthorityString(UserDetailsImpl userDetails);

}
