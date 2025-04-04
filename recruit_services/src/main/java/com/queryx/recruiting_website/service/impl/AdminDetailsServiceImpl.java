package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.*;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDAdminMapper;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import com.queryx.recruiting_website.mapper.TPMenuMapper;
import com.queryx.recruiting_website.service.TPMenuService;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;


@Service
public class AdminDetailsServiceImpl implements UserDetailsService {

    @Resource
    private TDAdminMapper adminMapper;
    @Resource
    private TPMenuMapper menuMapper;
    @Resource
    private TPMenuService menuService;
    @Resource
    private TDUserMapper userMapper;
    private static final String PHONE = "(^1[3-9]\\d{9}$)";
    private static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<TDAdmin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TDAdmin::getAdminUsername, username)
                .eq(TDAdmin::getAdminStatus, Common.STATUS_ENABLE);
        TDAdmin tdAdmin = adminMapper.selectOne(wrapper);
        if (Objects.isNull(tdAdmin)) {
            TDUser user = null;
            if (username.matches(PHONE)) {
                user = userMapper.selectOne(new LambdaQueryWrapper<TDUser>()
                        .eq(TDUser::getUserPhone, username).eq(TDUser::getUserStatus, Common.STATUS_ENABLE)
                        .eq(TDUser::getDelFlag, Common.NOT_DELETE));

            } else if (username.matches(EMAIL)) {
                user = userMapper.selectOne(new LambdaQueryWrapper<TDUser>()
                        .eq(TDUser::getUserEmail, username).eq(TDUser::getUserStatus, Common.STATUS_ENABLE)
                        .eq(TDUser::getDelFlag, Common.NOT_DELETE));
            }
            if (Objects.isNull(user)) {
                throw new SystemException(AppHttpCodeEnum.LOGIN_ERROR);
            }
            List<String> perms = menuMapper.selectPermsByRoleId(Long.valueOf(user.getUserRole()))
                    .stream()
                    .filter(StringUtils::hasText)
                    .toList();
            return new LoginUser(user, perms);
        }
        List<String> perms;
        if (tdAdmin.getRoleId() == 1) {
            perms = menuService.list(
                    new LambdaQueryWrapper<TPMenu>().eq(TPMenu::getStatus, Common.STATUS_ENABLE)
                            .eq(TPMenu::getDelFlag, Common.NOT_DELETE)
                    ).stream()
                    .map(TPMenu::getPerms)
                    .filter(StringUtils::hasText)
                    .toList();
        } else {
            perms = menuMapper.selectPermsByRoleId(tdAdmin.getRoleId()).stream()
                    .filter(StringUtils::hasText)
                    .toList();
        }

        return new LoginAdmin(tdAdmin, perms);
    }

}

