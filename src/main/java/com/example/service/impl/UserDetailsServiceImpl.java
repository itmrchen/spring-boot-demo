package com.example.service.impl;

import com.example.model.po.User;
import com.example.model.vo.UserDetailsImpl;
import com.example.params.Params;
import com.example.service.IUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private IUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(username)) {
            throw new UsernameNotFoundException("用户名username不能为空！");
        }
        User user = userService.customGetOne(new Params<>(new User().setUsername(username)));
        if (user==null) {
            throw new UsernameNotFoundException("用户名："+ username + "不存在！");
        }
        return new UserDetailsImpl(user);
    }

}
