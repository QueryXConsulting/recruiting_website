package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDResume;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.dto.RegisterDTO;
import com.queryx.recruiting_website.domain.vo.LoginVO;
import com.queryx.recruiting_website.mapper.ResumeMapper;
import com.queryx.recruiting_website.mapper.UserMapper;
import com.queryx.recruiting_website.service.UserService;
import com.queryx.recruiting_website.utils.JwtUtil;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    public final String USER_ID = "userId";
    public final String RESUME_ID = "resumeId";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String insertUser(RegisterDTO registerDTO) {
        // 装配resume对象
        final TDResume userResume = new TDResume();
        BeanUtils.copyProperties(registerDTO, userResume);
        // 插入在线简历
        // 装配user对象
        final TDUser user = new TDUser();
        user.setResumeId(userResume.getResumeId());
        user.setUserRegisterTime(Date.from(Instant.now()));
        BeanUtils.copyProperties(registerDTO, user);
        // 判断用户是否已注册 TODO 用户注册：待优化，验证码待实现
        Long users = userMapper.selectCount(new LambdaQueryWrapper<TDUser>()
                .eq(TDUser::getUserPhone, registerDTO.getResumePhone()));
        Long resumes = resumeMapper.selectCount(new LambdaQueryWrapper<TDResume>()
                .eq(TDResume::getResumeEmail, registerDTO.getResumeEmail()));
        if (users > 0 && resumes > 0) {
            return "1";
        }
        if (users > 0) return "2";
        if (resumes > 0) return "3";
        // 插入用户
        resumeMapper.insert(userResume);
        userMapper.insert(user);
        // 返回JWT
//        return JwtUtil.createJWT(Map.of(USER_ID, user.getUserId(), RESUME_ID, userResume.getResumeId()));
        return "注册成功";
    }

    @Override
    public String queryUser(LoginDTO loginDTO) {
        final String PHONE = "(^1[3-9]\\d{9}$)";
        final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        LoginVO loginVO = new LoginVO();
        // 判断用户名登录方式 TODO 用户登录：验证码待实现
        if (loginDTO.getUsername().matches(PHONE)) {
            TDUser user = userMapper.selectOne(new LambdaQueryWrapper<TDUser>()
                    .eq(TDUser::getUserPhone, loginDTO.getUsername())
                    .eq(TDUser::getUserPassword, loginDTO.getUserPassword())
                    .eq(TDUser::getUserRole, Common.USER_ROLE_GENERAL)
            );
            loginVO.setUserId(user.getUserId());
            loginVO.setResumeId(user.getResumeId());
        } else if (loginDTO.getUsername().matches(EMAIL)) {
            loginVO = userMapper.queryUserByEmail(loginDTO);
        }
        // 返回JWT
        return JwtUtil.createJWT(Map.of(USER_ID, loginVO.getUserId(), RESUME_ID, loginVO.getResumeId()));
    }
}