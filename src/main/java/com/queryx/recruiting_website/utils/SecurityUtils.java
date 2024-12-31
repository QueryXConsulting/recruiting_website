package com.queryx.recruiting_website.utils;


import com.queryx.recruiting_website.domain.LoginAdmin;
import com.queryx.recruiting_website.domain.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils
{
    public static LoginAdmin getLoginAdmin() {
        return (LoginAdmin) getAuthentication().getPrincipal();
    }

    public static LoginUser getLoginUser() {
        return (LoginUser) getAuthentication().getPrincipal();
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
