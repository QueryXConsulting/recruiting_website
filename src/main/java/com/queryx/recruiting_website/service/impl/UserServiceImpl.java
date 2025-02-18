package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.TDResume;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.design.LoginContext;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.dto.RegisterDTO;
import com.queryx.recruiting_website.domain.vo.LoginVO;
import com.queryx.recruiting_website.mapper.TDResumeMapper;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import com.queryx.recruiting_website.service.UserService;
import com.queryx.recruiting_website.utils.JwtUtil;
import com.queryx.recruiting_website.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    final String timeZone = "Asia/Shanghai";

    @Autowired
    private TDUserMapper userMapper;

    @Autowired
    private TDResumeMapper resumeMapper;


    public static final String PHONE = "(^1[3-9]\\d{9}$)";
    public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    //    final boolean  =
    public final String USER_ID = "userId";
    public final String RESUME_ID = "resumeId";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppHttpCodeEnum insertUser(RegisterDTO registerDTO) {
        // 判断手机号或邮箱是否合法
        boolean b = registerDTO.getResumePhone().matches(PHONE) && registerDTO.getResumeEmail().matches(EMAIL);
        if (!b) {
            return AppHttpCodeEnum.PHONE_OR_EMAIL_ILLEGAL;
        }
        final TDResume userResume = new TDResume();
        final TDUser user = new TDUser();
        // 判断用户是否已注册 TODO 用户注册：待优化，验证码待实现
        Long users = userMapper.selectCount(new LambdaQueryWrapper<TDUser>()
                .eq(TDUser::getUserPhone, registerDTO.getResumePhone()));
        Long resumes = resumeMapper.selectCount(new LambdaQueryWrapper<TDResume>()
                .eq(TDResume::getResumeEmail, registerDTO.getResumeEmail()));
        if (users > 0 && resumes > 0) return AppHttpCodeEnum.USER_EXIST;
        if (users > 0) return AppHttpCodeEnum.PHONE_EXIST;
        if (resumes > 0) return AppHttpCodeEnum.EMAIL_EXIST;
        // 复制属性
        user.setResumeId(userResume.getResumeId());
        user.setUserRegisterTime(Date.from(ZonedDateTime.now(ZoneId.of(timeZone)).toInstant()));
        BeanUtils.copyProperties(registerDTO, user);
        BeanUtils.copyProperties(registerDTO, userResume);
        // 插入用户
        resumeMapper.insert(userResume);
        userMapper.insert(user);
        // 返回JWT
//        return JwtUtil.createJWT(Map.of(USER_ID, user.getUserId(), RESUME_ID, userResume.getResumeId()));
        return AppHttpCodeEnum.SUCCESS;
    }

    @Override
    public String queryUser(LoginDTO loginDTO) {
        LoginVO loginVO = LoginContext.executeLogin(userMapper, loginDTO);
        if (loginVO == null) {
            return null;
        }
        // 返回JWT
        return JwtUtil.createJWT(SecurityUtils.getLoginUser().getTdUser().getUserId());
    }
}
