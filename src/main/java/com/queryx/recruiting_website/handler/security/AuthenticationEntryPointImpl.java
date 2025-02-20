package com.queryx.recruiting_website.handler.security;


import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.SecurityUtils;
import com.queryx.recruiting_website.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Slf4j
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

        log.error("认证或授权失败", e);
        CommonResp<?> result = null;
        if (e instanceof BadCredentialsException) {
            result =
                    CommonResp.fail(AppHttpCodeEnum.LOGIN_ERROR
                            , null);
        } else if (e instanceof InsufficientAuthenticationException) {
            result =CommonResp.fail(AppHttpCodeEnum.NEED_LOGIN,null);
        }else {
            result=CommonResp
                    .fail(AppHttpCodeEnum.NO_OPERATOR_AUTH,null);
        }
        // 响应给前端
        WebUtils.renderString(response, SecurityUtils.convertCommonRespToJson(result));

    }
}
