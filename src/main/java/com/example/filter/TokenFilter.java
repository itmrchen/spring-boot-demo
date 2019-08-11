package com.example.filter;

import com.example.model.vo.ResultVO;
import com.example.model.vo.UserDetailsImpl;
import com.example.service.ITokenService;
import com.example.util.ResponseUtil;
import io.jsonwebtoken.MalformedJwtException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.model.vo.ResultVO.UNAUTHORIZED;

@Component
public class TokenFilter extends OncePerRequestFilter {

    public static final String TOKEN_KEY = "token";
    private static final Long MINUTES_10 = 10 * 60 * 1000L;
    private static final RequestMatcher[] IGNORE_PATH = {
            new AntPathRequestMatcher("/login", "POST"), // 登录
            new AntPathRequestMatcher("/logout", "GET"), // 登出
            new AntPathRequestMatcher("/userinfo", "POST"), // 注册
    };
    @Autowired
    private ITokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token) && !"null".equals(token)) {
            try {
                UserDetailsImpl userDetails = tokenService.getUserDetalis(token);
                if (userDetails == null) {
                    boolean flag = true;
                    for (RequestMatcher requestMatcher : IGNORE_PATH) {
                        if (requestMatcher.matches(request)) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        ResultVO resultVO = new ResultVO<>(UNAUTHORIZED, "登录过期！", null);
                        ResponseUtil.println(response, resultVO);
                        return;
                    }
                } else {
                    checkExpireTime(userDetails);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (MalformedJwtException e) {
                ResultVO resultVO = new ResultVO<>(UNAUTHORIZED, "非法认证！", null);
                ResponseUtil.println(response, resultVO);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    // 过期时间与当前时间对比，临近过期10分钟内的话，自动刷新缓存
    private void checkExpireTime(UserDetailsImpl userDetails) {
        long expireTime = userDetails.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MINUTES_10) {
            tokenService.refresh(userDetails);
        }
    }

    // 根据参数或者header获取token
    public static String getToken(HttpServletRequest request) {
        String token = request.getParameter(TOKEN_KEY);
        if (StringUtils.isEmpty(token)) {
            token = request.getHeader(TOKEN_KEY);
        }
        return token;
    }

}
