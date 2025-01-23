package com.queryx.recruiting_website.filter;

import com.queryx.recruiting_website.config.DynamicAuthorizationManager;
import com.queryx.recruiting_website.config.DynamicSecurityMetadataSource;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class DynamicSecurityFilter extends OncePerRequestFilter {

    @Resource
    private DynamicSecurityMetadataSource securityMetadataSource;

    @Resource
    private DynamicAuthorizationManager authorizationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException, IOException {

        if (shouldNotFilter(request)) {
            chain.doFilter(request, response);
            return;
        }

        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            RequestAuthorizationContext context = new RequestAuthorizationContext(request);

            // 权限检查
           authorizationManager.check(
                    () -> authentication,
                    context
            );

            // 通过权限检查，继续处理请求
            chain.doFilter(request, response);

        } catch (AccessDeniedException e) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"code\":403,\"message\":\"没有访问权限\"}");
        } catch (Exception e) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"code\":500,\"message\":\"服务器内部错误\"}");
        }
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        List<String> whitelist = Arrays.asList(
                "/admin/getRouters",
                "/admin/getInfo",
                "/user/login",
                "/admin/menuList",
                "/admin/selectRoleMenusTree",
                "/admin/role"
        );

        String path = request.getRequestURI();
        return whitelist.stream()
                .anyMatch(pattern ->
                        pattern.endsWith("/**")
                                ? path.startsWith(pattern.substring(0, pattern.length() - 3))
                                : pattern.equals(path)
                );
    }
}
