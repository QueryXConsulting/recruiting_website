package com.queryx.recruiting_website.filter;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.LoginAdmin;
import com.queryx.recruiting_website.domain.LoginUser;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.SecurityUtils;
import com.queryx.recruiting_website.utils.TokenStorage;
import com.queryx.recruiting_website.utils.WebUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        Object user = TokenStorage.getUser(token);
        try {
            // 校验是否过期
            if (ObjectUtils.isEmpty(user)) {
                TokenStorage.removeToken(token);
                String result = SecurityUtils.convertCommonRespToJson(CommonResp.fail(AppHttpCodeEnum.NEED_LOGIN, null));
                WebUtils.renderString(response, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String result = SecurityUtils.convertCommonRespToJson(CommonResp.fail(AppHttpCodeEnum.NEED_LOGIN,null));
            WebUtils.renderString(response, result);
        }



        if (user instanceof LoginUser loginUser) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            filterChain.doFilter(request, response);
            return;
        }

        LoginAdmin loginAdmin = (LoginAdmin) user;
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(loginAdmin,  null, loginAdmin.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        filterChain.doFilter(request, response);

    }

}
