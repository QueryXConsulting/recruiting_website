package com.queryx.recruiting_website.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.LoginAdmin;
import com.queryx.recruiting_website.domain.LoginUser;
import com.queryx.recruiting_website.domain.TDAdmin;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.utils.CommonResp;

import com.queryx.recruiting_website.utils.JwtUtil;
import com.queryx.recruiting_website.utils.SecurityUtils;
import com.queryx.recruiting_website.utils.WebUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        Claims data = null;

        try {
            data = JwtUtil.parseJWT(token).getPayload();
            Date expiration = data.getExpiration();
            // 校验是否过期
            if (expiration.before(new Date())) {
                String result = SecurityUtils.convertCommonRespToJson(CommonResp.fail(AppHttpCodeEnum.LOGIN_EXPIRED, null));
                WebUtils.renderString(response, result);
            }
        } catch (Exception e) {
            log.error("token解析失败", e);
            String result = SecurityUtils.convertCommonRespToJson(CommonResp.fail(AppHttpCodeEnum.NEED_LOGIN, null));
            WebUtils.renderString(response, result);
        }


        LinkedHashMap adminUser = (LinkedHashMap) data.get("AdminUser");
        if (ObjectUtils.isEmpty(adminUser)) {
            LinkedHashMap user = (LinkedHashMap) data.get("User");
            LoginUser loginUser = getLoginUser(user);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            filterChain.doFilter(request, response);
            return;
        }

        LoginAdmin loginAdmin = getLoginAdmin(adminUser);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(loginAdmin, null, loginAdmin.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        filterChain.doFilter(request, response);
    }

    private LoginAdmin getLoginAdmin(LinkedHashMap adminUser) {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginAdmin loginAdmin = new LoginAdmin();
        TDAdmin tdAdmin = objectMapper.convertValue(adminUser.get("tdAdmin"), TDAdmin.class);
        loginAdmin.setTdAdmin(tdAdmin);
        loginAdmin.setPermissions((List<String>) adminUser.get("permissions"));
        return loginAdmin;
    }

    private LoginUser getLoginUser(LinkedHashMap user) {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginUser loginUser = new LoginUser();
        TDUser tdUser = objectMapper.convertValue(user.get("tdUser"), TDUser.class);
        loginUser.setTdUser(tdUser);
        loginUser.setPermissions((List<String>) user.get("permissions"));
        return loginUser;
    }


}
