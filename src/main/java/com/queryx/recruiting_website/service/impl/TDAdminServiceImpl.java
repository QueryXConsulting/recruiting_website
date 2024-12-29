package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.LoginAdmin;
import com.queryx.recruiting_website.domain.TDAdmin;
import com.queryx.recruiting_website.domain.TDUser;

import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDAdminMapper;

import com.queryx.recruiting_website.service.TDAdminService;
import com.queryx.recruiting_website.vo.AdminLoginVo;

import com.queryx.recruiting_website.vo.AdminVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Objects;


/**
 * (TDAdmin)表服务实现类
 *
 * @author makejava
 * @since 2024-12-23 13:11:00
 */
@Service("tDAdminService")
public class TDAdminServiceImpl extends ServiceImpl<TDAdminMapper, TDAdmin> implements TDAdminService {

    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private TDAdminMapper tdAdminMapper;

    @Override
    public AdminVo addAdmin(AdminVo adminVo) {
        if (!StringUtils.hasText(adminVo.getAdminUsername())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }

        if (!StringUtils.hasText(adminVo.getAdminPassword())) {
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }

        LambdaQueryWrapper<TDAdmin> tdAdminLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tdAdminLambdaQueryWrapper.eq(TDAdmin::getAdminUsername, adminVo.getAdminUsername());
        if (count(tdAdminLambdaQueryWrapper) > 0) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }

        String password = passwordEncoder.encode(adminVo.getAdminPassword());
        adminVo.setAdminPassword(password);
        TDAdmin tdAdmin = new TDAdmin();
        BeanUtils.copyProperties(adminVo, tdAdmin);
        tdAdmin.setAdminStatus("0");
        tdAdmin.setAdminCreateTime(new Date());
        if (!save(tdAdmin)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }

        return null;
    }

    @Override
    public Object login(AdminLoginVo adminLoginVo) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(adminLoginVo.getAdminUsername(), adminLoginVo.getAdminPassword());
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        // 判断是否验证通过
        if (Objects.isNull(authenticate)) {
            throw new SystemException(AppHttpCodeEnum.LOGIN_ERROR);
        }
        LoginAdmin loginUser = (LoginAdmin) authenticate.getPrincipal();
        // TODO 返回前端凭证(待续)

        return null;
    }
}


