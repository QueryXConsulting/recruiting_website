package com.queryx.recruiting_website.config;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Resource
    private DynamicSecurityMetadataSource securityMetadataSource;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();

        // 获取当前请求所需的权限
        FilterInvocation fi = new FilterInvocation(request, null, null);
        Collection<ConfigAttribute> attributes = securityMetadataSource.getAttributes(fi);

        // 没有配置权限要求，允许访问
        if (CollectionUtils.isEmpty(attributes)) {
            return new AuthorizationDecision(true);
        }

        // 获取当前用户认证信息
        Authentication auth = authentication.get();


        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }



        // 获取用户所拥有的权限
        Set<String> userPermissions = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        // 判断是否有所需权限
        boolean hasPermission = attributes.stream()
            .map(ConfigAttribute::getAttribute)
            .anyMatch(userPermissions::contains);

        return new AuthorizationDecision(hasPermission);
    }
}
