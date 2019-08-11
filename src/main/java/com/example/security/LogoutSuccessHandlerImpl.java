package com.example.security;

import com.example.filter.TokenFilter;
import com.example.model.vo.ResultVO;
import com.example.service.ITokenService;
import com.example.util.ResponseUtil;
import io.jsonwebtoken.MalformedJwtException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

import static com.example.model.vo.ResultVO.SUCCESS;
import static com.example.model.vo.ResultVO.UNAUTHORIZED;

// 退出登录成功
@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Autowired
    private ITokenService tokenService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String token = TokenFilter.getToken(request);
        if (!StringUtils.isEmpty(token) && !"null".equals(token)) {
            try {
                tokenService.deleteToken(token);
            } catch (MalformedJwtException e) {
                ResultVO resultVO = new ResultVO<>(UNAUTHORIZED, "非法认证！", null);
                ResponseUtil.println(response, resultVO);
                return;
            }
        }
        // 返回游客资源
        Set<String> authorities = tokenService.getAuthorityString(tokenService.getGuest());
        ResultVO<Set<String>> resultVO = new ResultVO<>(SUCCESS, "退出成功！", authorities);
        ResponseUtil.println(response, resultVO);
    }

}
