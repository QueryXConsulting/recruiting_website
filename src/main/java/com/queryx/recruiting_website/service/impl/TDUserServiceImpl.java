package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDJob;
import com.queryx.recruiting_website.domain.TDUser;

import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDUserMapper;

import com.queryx.recruiting_website.service.TDUserService;

import com.queryx.recruiting_website.vo.UserCompanyVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


/**
 * (TDUser)表服务实现类
 *
 * @author makejava
 * @since 2024-12-23 13:11:00
 */
@Service("tDUserService")
public class TDUserServiceImpl extends ServiceImpl<TDUserMapper, TDUser> implements TDUserService {
    @Resource
    private TDUserMapper tdUserMapper;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public UserCompanyVo selectUserInfo(Long userId, String userRole) {
        LambdaQueryWrapper<TDUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(TDUser::getUserId, userId)
                .eq(TDUser::getUserRole, userRole)
                .eq(TDUser::getUserStatus,'0');
        TDUser tdUser = tdUserMapper.selectOne(userLambdaQueryWrapper);

        UserCompanyVo userCompanyVo = new UserCompanyVo();
        BeanUtils.copyProperties(tdUser, userCompanyVo);
        return userCompanyVo;
    }

    @Override
    public UserCompanyVo updateUserInfo(UserCompanyVo userCompanyVo) {
        if (!StringUtils.hasText(userCompanyVo.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if (!StringUtils.hasText(userCompanyVo.getUserPassword())) {
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if (!StringUtils.hasText(userCompanyVo.getUserPhone())){
            throw new SystemException(AppHttpCodeEnum.PHONE_NULL);
        }

        TDUser tdUser = new TDUser();
        BeanUtils.copyProperties(userCompanyVo, tdUser);
        tdUser.setUserPassword(passwordEncoder.encode(tdUser.getUserPassword()));
        UpdateWrapper<TDUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userCompanyVo.getUserId())
                .eq("user_status", "0");
        if (!update(tdUser,updateWrapper)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

}


