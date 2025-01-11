package com.queryx.recruiting_website.handler.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.LoginAdmin;
import com.queryx.recruiting_website.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
public class CustomPermissionEvaluator implements PermissionEvaluator {
    private static final Logger log = LoggerFactory.getLogger(CustomPermissionEvaluator.class);


    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        // 自定义权限校验
        log.warn("----------------进入权限校验---------------------");
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginAdmin loginAdmin) {
            if (loginAdmin.getTdAdmin().getRoleId().equals(Long.valueOf(Common.SUPER_ADMIN.getCode()))) {
                return true;
            }
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(permission));
    }


    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

        return false;
    }
}
