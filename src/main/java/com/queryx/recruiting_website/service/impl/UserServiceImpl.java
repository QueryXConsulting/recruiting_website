package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDResume;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.design.LoginContext;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.dto.RegisterDTO;
import com.queryx.recruiting_website.domain.vo.LoginVO;
import com.queryx.recruiting_website.mapper.TDResumeMapper;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import com.queryx.recruiting_website.service.UserService;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    final String timeZone = "Asia/Shanghai";

    @Value("${file.upload-path-avatar}")
    private String uploadPathAvatar;

    @Autowired
    private TDUserMapper userMapper;

    @Autowired
    private TDResumeMapper resumeMapper;


    public static final String PHONE = "(^1[3-9]\\d{9}$)";
    public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

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
        // 检查查询结果
        final Long defaultCount = 0L;
        users = Objects.requireNonNullElse(users, defaultCount);
        resumes = Objects.requireNonNullElse(resumes, defaultCount);

        if (users > 0 && resumes > 0) return AppHttpCodeEnum.USER_EXIST;
        if (users > 0) return AppHttpCodeEnum.PHONE_EXIST;
        if (resumes > 0) return AppHttpCodeEnum.EMAIL_EXIST;
        // 复制属性
        user.setResumeId(userResume.getResumeId());
        user.setUserRegisterTime(Calendar.getInstance(Locale.CHINA).getTime());
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
        return JwtUtil.createJWT(Map.of(USER_ID, loginVO.getUserId(), RESUME_ID, loginVO.getResumeId()));
    }

    @Override // TODO 未测试
    public CommonResp<String> uploadAvatar(String userId, MultipartFile image) {
        // 查询用户是否存在头像
        TDUser user = userMapper.selectById(userId);
        // 非法用户
        if (Common.DELETE.equals(user.getDelFlag()) && Common.STATUS_DISABLE.equals(user.getUserStatus())) {
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        // 有 -> 删除
        if (user.getUserAvatar() != null || user.getUserAvatar().trim().isEmpty()) {
            File oldFile = new File(uploadPathAvatar + user.getUserAvatar());
            if (!(oldFile.exists() && oldFile.delete())) {
                return CommonResp.fail(AppHttpCodeEnum.AVATAR_DELETE_ERROR, null);
            }
        }
        // 保存头像图片
        String fileName = image.getOriginalFilename();
        String newFileName = System.currentTimeMillis() + "_" + fileName;
        File file = new File(uploadPathAvatar + newFileName);
        try {
            image.transferTo(file);
            if (!file.exists()) {
                return CommonResp.fail(AppHttpCodeEnum.AVATAR_UPLOAD_ERROR, null);
            }
            // 更新数据库
            user.setUserAvatar(newFileName);
            userMapper.updateById(user);
        } catch (Exception e) {
            return CommonResp.fail(AppHttpCodeEnum.AVATAR_UPLOAD_ERROR, null);
        }
        return CommonResp.success(null);
    }
}
