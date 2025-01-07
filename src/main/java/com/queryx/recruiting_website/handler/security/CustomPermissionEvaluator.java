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

@Configuration
public class CustomPermissionEvaluator implements PermissionEvaluator {
    private static final Logger log = LoggerFactory.getLogger(CustomPermissionEvaluator.class);


    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        // 自定义权限校验
        log.error("----------------进入权限校验---------------------");
        // 普通用户应无任何权限则直接返回false
        if (authentication.getAuthorities().isEmpty()) {
            return false;
        }
        // 管理员用户则进行权限校验
        LoginAdmin loginAdmin = SecurityUtils.getLoginAdmin();
        if (loginAdmin.getTdAdmin().getRoleId().equals(Long.valueOf(Common.SUPER_ADMIN.getCode()))) {
            return true;
        }
        List<String> permissions = loginAdmin.getPermissions();
        return permissions.contains(permission);
    }


    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

        return false;
    }
}
