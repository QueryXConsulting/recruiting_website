package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.LoginAdmin;
import com.queryx.recruiting_website.domain.TDAdmin;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDAdminMapper;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class AdminDetailsServiceImpl implements UserDetailsService {

    @Resource
    private TDAdminMapper adminMapper;

    @Override
    public UserDetails loadUserByUsername(String userPhone) throws UsernameNotFoundException {

        LambdaQueryWrapper<TDAdmin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TDAdmin::getAdminUsername,userPhone).eq(TDAdmin::getAdminStatus, "0");
        TDAdmin tdAdmin = adminMapper.selectOne(wrapper);
        if(Objects.isNull(tdAdmin)){
            throw new SystemException(AppHttpCodeEnum.LOGIN_ERROR);
        }
        // TODO 权限查询并封装(待续)

        return new LoginAdmin(tdAdmin,null);
    }
}
