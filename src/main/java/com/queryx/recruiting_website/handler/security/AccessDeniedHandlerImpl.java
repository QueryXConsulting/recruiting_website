package com.queryx.recruiting_website.handler.security;


import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.utils.CommonResp;

import com.queryx.recruiting_website.utils.SecurityUtils;
import com.queryx.recruiting_website.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {

        e.printStackTrace();
        CommonResp result = CommonResp.fail(AppHttpCodeEnum.NO_OPERATOR_AUTH.getCode(),AppHttpCodeEnum.NO_OPERATOR_AUTH.getMsg());
        // 响应给前端
        WebUtils.renderString(response, SecurityUtils.convertCommonRespToJson(result));

    }


}
