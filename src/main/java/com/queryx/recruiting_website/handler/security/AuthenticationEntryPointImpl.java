package com.queryx.recruiting_website.handler.security;


import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.SecurityUtils;
import com.queryx.recruiting_website.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

        e.printStackTrace();
        CommonResp result = null;
        if (e instanceof BadCredentialsException) {
            result =
                    CommonResp.fail(AppHttpCodeEnum.LOGIN_ERROR.getCode()
                            , e.getMessage());
        } else if (e instanceof InsufficientAuthenticationException) {
            result =CommonResp.fail(AppHttpCodeEnum.NEED_LOGIN.getCode(),AppHttpCodeEnum.NEED_LOGIN.getMsg());
        }else {
            result=CommonResp
                    .fail(AppHttpCodeEnum.SYSTEM_ERROR.getCode(),"认证或授权失败");
        }
        // 响应给前端
        WebUtils.renderString(response, SecurityUtils.convertCommonRespToJson(result));

    }
}
