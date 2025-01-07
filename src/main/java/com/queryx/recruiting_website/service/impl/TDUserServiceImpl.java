package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.*;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.dto.UserDto;
import com.queryx.recruiting_website.domain.dto.UserRegisterDTO;
import com.queryx.recruiting_website.domain.vo.JobCompanyListVo;
import com.queryx.recruiting_website.domain.vo.UserLoginVo;
import com.queryx.recruiting_website.domain.vo.UserVo;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDAdminMapper;
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
import java.util.stream.Collectors;


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
    @Resource
    private TDAdminMapper adminMapper;

    @Override
    public UserCompanyDto selectUserInfo(Long userId, String userRole) {
        LambdaQueryWrapper<TDUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(TDUser::getUserId, userId)
                .eq(TDUser::getDelFlag, Common.NOT_DELETED.getCode());
        if (StringUtils.hasText(userRole)) {
            userLambdaQueryWrapper.eq(TDUser::getUserRole, userRole)
                    .eq(TDUser::getUserStatus, Common.STATUS_ENABLE.getCode());
        }


        TDUser tdUser = tdUserMapper.selectOne(userLambdaQueryWrapper);

        UserCompanyDto userCompanyDto = new UserCompanyDto();
        BeanUtils.copyProperties(tdUser, userCompanyDto);
        return userCompanyDto;
    }

    @Override
    public UserCompanyDto updateUserCompanyInfo(UserCompanyDto userCompanyDto) {
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
                .eq("user_status", Common.STATUS_ENABLE.getCode())
                .eq("del_flag", Common.NOT_DELETED.getCode());
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

        UserLoginVo userLoginVo = new UserLoginVo();
        // 判断是否为公司用户
        if (loginDTO.getUserRole().equals("1")) {
            LambdaQueryWrapper<TDCompanyInfo> companyInfoQuery = new LambdaQueryWrapper<>();
            companyInfoQuery.eq(TDCompanyInfo::getUserId, loginUser.getTdUser().getUserId())
                    .select(TDCompanyInfo::getCompanyInfoId);
            TDCompanyInfo tdCompanyInfo = companyInfoMapper.selectOne(companyInfoQuery);
            Long companyId = null;
            if (!Objects.isNull(tdCompanyInfo)) {
                companyId = tdCompanyInfo.getCompanyInfoId();
            }
            userLoginVo.setCompanyId(companyId);
        }

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
                .eq(TDUser::getDelFlag, Common.NOT_DELETED.getCode());
        if (count(queryWrapper) > 0) {
            throw new SystemException(AppHttpCodeEnum.PHONE_EXIST);
        }

        LambdaQueryWrapper<TDResume> Wrapper = new LambdaQueryWrapper<>();
        Wrapper.eq(TDResume::getResumeEmail, userRegisterDTO.getResumeEmail());
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

    @Override
    public Page<UserVo> selectUserList(Integer page, Integer size) {
        LambdaUpdateWrapper<TDUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TDUser::getDelFlag, Common.NOT_DELETED.getCode());
        Page<TDUser> tdUserPage = tdUserMapper.selectPage(new Page<>(page, size), wrapper);
        Page<UserVo> userVoPage = new Page<>(tdUserPage.getCurrent(), tdUserPage.getSize(), tdUserPage.getTotal());

        userVoPage.setRecords(tdUserPage.getRecords().stream().map(user -> {
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            return userVo;
        }).collect(Collectors.toList()));

        return userVoPage;

    }

    @Override
    public Object updateUserInfo(UserDto userDto) {
        if (!StringUtils.hasText(userDto.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if (!StringUtils.hasText(userDto.getUserPassword())) {
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if (!StringUtils.hasText(userDto.getUserPhone())) {
            throw new SystemException(AppHttpCodeEnum.PHONE_NULL);
        }

        TDUser tdUser = new TDUser();
        BeanUtils.copyProperties(userDto, tdUser);
        tdUser.setUserPassword(passwordEncoder.encode(tdUser.getUserPassword()));
        if (!update(tdUser, new LambdaUpdateWrapper<TDUser>().eq(TDUser::getUserId,
                userDto.getUserId()).eq(TDUser::getDelFlag,Common.NOT_DELETED.getCode()))) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }

        return null;
    }

    @Override
    public Object deleteUser(Long userId) {
        LambdaUpdateWrapper<TDUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TDUser::getUserId, userId)
                .set(TDUser::getDelFlag, Common.DELETED.getCode());
        if (!update(wrapper)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public Object addUser(UserDto userDto) {
        LambdaQueryWrapper<TDUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TDUser::getUserPhone, userDto.getUserPhone())
                .eq(TDUser::getDelFlag, Common.NOT_DELETED.getCode());
        if (count(queryWrapper) > 0) {
            throw new SystemException(AppHttpCodeEnum.PHONE_EXIST);
        }
        TDUser tdUser = new TDUser();
        BeanUtils.copyProperties(userDto, tdUser);
        tdUser.setUserRegisterTime(new Date());
        tdUser.setUserPassword(passwordEncoder.encode(tdUser.getUserPassword()));
        if (!save(tdUser)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }


}


