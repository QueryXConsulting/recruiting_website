package com.queryx.recruiting_website.filter;

import com.queryx.recruiting_website.config.DynamicAuthorizationManager;
import com.queryx.recruiting_website.config.DynamicSecurityMetadataSource;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
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

    @Resource
    private AccessDeniedHandler accessDeniedHandler;

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
            AuthorizationDecision check = authorizationManager.check(
                    () -> authentication,
                    context
            );
            if (check.isGranted()) {
                chain.doFilter(request, response);
            } else {
                accessDeniedHandler.handle(request, response, new AccessDeniedException("权限不足"));
            }

        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        List<String> whitelist = Arrays.asList(
                "/admin/getRouters",
                "/admin/getInfo",
                "/user/login",
                "/user/register",
                "/admin/menuList",
                "/admin/selectRoleMenusTree",
                "/admin/role",
                "/avatar_files/**",
                "/offer_files/**",
                "/resume_files/**",
                "/signature_files/**",
                "/company/registerCompany",
                "/company/offer/save",
                "/offer_Template/**",
                "/query/jobs",
                "/query/jobNatureList",
                "/query/companyList"
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
