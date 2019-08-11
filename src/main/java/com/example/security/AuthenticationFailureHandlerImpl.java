package com.example.security;

import com.example.model.vo.ResultVO;
import com.example.util.ResponseUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.model.vo.ResultVO.UNAUTHORIZED;

// 认证失败
@Component
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String message;
        if (exception instanceof UsernameNotFoundException || exception instanceof BadCredentialsException) {
            message = "用户名或密码错误！";
        } else if (exception instanceof DisabledException) {
            message = "账户被禁用！";
        } else {
            message = "登录失败！";
        }
        ResultVO resultVO = new ResultVO<>(UNAUTHORIZED, message, null);
        ResponseUtil.println(response, resultVO);
    }

}
