package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.*;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.dto.UserRegisterDTO;
import com.queryx.recruiting_website.domain.vo.UserLoginVo;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDCompanyInfoMapper;
import com.queryx.recruiting_website.mapper.TDResumeMapper;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import com.queryx.recruiting_website.service.TDUserService;
import com.queryx.recruiting_website.domain.dto.UserCompanyDto;
import com.queryx.recruiting_website.utils.JwtUtil;
import com.queryx.recruiting_website.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


@Service("tDUserService")
public class TDUserServiceImpl extends ServiceImpl<TDUserMapper, TDUser> implements TDUserService {
    @Resource
    private TDUserMapper tdUserMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private TDResumeMapper resumeMapper;
    @Resource
    private TDCompanyInfoMapper companyInfoMapper;

    @Override
    public UserCompanyDto selectUserInfo(Long userId, String userRole) {
        LambdaQueryWrapper<TDUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(TDUser::getUserId, userId)
                .eq(TDUser::getUserRole, userRole)
                .eq(TDUser::getUserStatus, '0');
        TDUser tdUser = tdUserMapper.selectOne(userLambdaQueryWrapper);

        UserCompanyDto userCompanyDto = new UserCompanyDto();
        BeanUtils.copyProperties(tdUser, userCompanyDto);
        return userCompanyDto;
    }

    @Override
    public UserCompanyDto updateUserInfo(UserCompanyDto userCompanyDto) {
        if (!StringUtils.hasText(userCompanyDto.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if (!StringUtils.hasText(userCompanyDto.getUserPassword())) {
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if (!StringUtils.hasText(userCompanyDto.getUserPhone())) {
            throw new SystemException(AppHttpCodeEnum.PHONE_NULL);
        }
        TDUser tdUser = SecurityUtils.getLoginUser().getTdUser();
        BeanUtils.copyProperties(userCompanyDto, tdUser);
        tdUser.setUserPassword(passwordEncoder.encode(tdUser.getUserPassword()));
        UpdateWrapper<TDUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", tdUser.getUserId())
                .eq("user_status", "0");
        if (!update(tdUser, updateWrapper)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public UserLoginVo login(LoginDTO loginDTO) {
        //  TODO 用户登录：(验证码待实现) 判断用户名登录方式
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getUserPassword());
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        // 判断是否验证通过
        if (Objects.isNull(authenticate)) {
            throw new SystemException(AppHttpCodeEnum.LOGIN_ERROR);
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        if (!loginDTO.getUserRole().equals(loginUser.getTdUser().getUserRole())) {
            throw new SystemException(AppHttpCodeEnum.LOGIN_ERROR);
        }
        LambdaQueryWrapper<TDCompanyInfo> companyInfoQuery = new LambdaQueryWrapper<>();
        companyInfoQuery.eq(TDCompanyInfo::getUserId, loginUser.getTdUser().getUserId())
                .select(TDCompanyInfo::getCompanyInfoId);
        TDCompanyInfo tdCompanyInfo = companyInfoMapper.selectOne(companyInfoQuery);
        Long companyId = null;
        if (!Objects.isNull(tdCompanyInfo)) {
            companyId = tdCompanyInfo.getCompanyInfoId();
        }
        UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setCompanyId(companyId);
//         生成token
        HashMap<String, Object> data = new HashMap<>();
        data.put("User", loginUser);
        userLoginVo.setToken(JwtUtil.createJWT(data));
//         返回前端凭证
        return userLoginVo;
    }

    @Override
    public UserRegisterDTO register(UserRegisterDTO userRegisterDTO) {
        LambdaQueryWrapper<TDUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TDUser::getUserPhone, userRegisterDTO.getUserPhone())
                .eq(TDUser::getUserStatus, "0");
        if (count(queryWrapper) > 0) {
            throw new SystemException(AppHttpCodeEnum.PHONE_EXIST);
        }

        LambdaQueryWrapper<TDResume> Wrapper = new LambdaQueryWrapper<>();
        Wrapper.eq(TDResume::getResumeEmail, userRegisterDTO.getResumeEmail())
                .eq(TDResume::getResumeStatus, "0")
                .eq(TDResume::getResumeReview, Common.REVIEW_SUCCESS.getCode());
        if (!Objects.isNull(resumeMapper.selectOne(Wrapper))) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }

        TDUser user = new TDUser();
        user.setUserRegisterTime(Date.from(Instant.now()));
        userRegisterDTO.setUserPassword(passwordEncoder.encode(userRegisterDTO.getUserPassword()));
        BeanUtils.copyProperties(userRegisterDTO, user);
        if (userRegisterDTO.getUserRole().equals("0")) {
            TDResume userResume = new TDResume();
            BeanUtils.copyProperties(userRegisterDTO, userResume);
            // 插入在线简历
            resumeMapper.insert(userResume);
            user.setResumeId(userResume.getResumeId());
            // 插入用户
            tdUserMapper.insert(user);
            return null;
        }
        // 插入用户
        tdUserMapper.insert(user);
        return null;
    }

}


