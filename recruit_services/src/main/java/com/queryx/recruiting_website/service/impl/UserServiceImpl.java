package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.dto.RegisterDTO;
import com.queryx.recruiting_website.domain.vo.UserLoginVO;
import com.queryx.recruiting_website.mapper.TDResumeMapper;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import com.queryx.recruiting_website.service.TDUserService;
import com.queryx.recruiting_website.service.UserService;
import com.queryx.recruiting_website.utils.CommonResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    final String timeZone = "Asia/Shanghai";

    @Autowired
    private TDUserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder; // 密码加密

    @Autowired
    private TDResumeMapper resumeMapper;

    @Autowired
    private TDUserService adminUserService;

    @Value("${file.upload-path-avatar}")
    private String uploadPathAvatar;

    public static final String PHONE = "(^1[3-9]\\d{9}$)";
    public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResp<UserLoginVO> insertUser(RegisterDTO registerDTO) {
        // 判断手机号或邮箱是否合法
        boolean b = registerDTO.getUserPhone().matches(PHONE) && registerDTO.getUserEmail().matches(EMAIL);
        if (!b) {
            return CommonResp.fail(AppHttpCodeEnum.PHONE_OR_EMAIL_ILLEGAL, null);
        }
        final TDUser user = new TDUser();
        // 判断用户是否已注册 TODO 用户注册：待优化，验证码待实现
        Long phones = userMapper.selectCount(new LambdaQueryWrapper<TDUser>()
                .select(TDUser::getUserId)
                .eq(TDUser::getUserPhone, registerDTO.getUserPhone()));
        Long emails = userMapper.selectCount(new LambdaQueryWrapper<TDUser>()
                .select(TDUser::getUserId)
                .eq(TDUser::getUserEmail, registerDTO.getUserEmail()));
        if (phones > 0) return CommonResp.fail(AppHttpCodeEnum.PHONE_EXIST, null);
        if (emails > 0) return CommonResp.fail(AppHttpCodeEnum.EMAIL_EXIST, null);
        // 复制属性
        BeanUtils.copyProperties(registerDTO, user);
        user.setUserRegisterTime(Date.from(ZonedDateTime.now(ZoneId.of(timeZone)).toInstant()));
        // 设置默认值
        user.setUserRole(Common.ROLE_USER);
        // 加密密码
        user.setUserPassword(passwordEncoder.encode(registerDTO.getUserPassword()));
        // 插入用户
        userMapper.insert(user);

        // 注册成功后，自动登录
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(registerDTO.getUserPhone());
        loginDTO.setUserPassword(registerDTO.getUserPassword());
        return CommonResp.success(adminUserService.login(loginDTO));
    }


    @Override
    public CommonResp<String> uploadAvatar(Long userId, MultipartFile image) {
        // 查询用户是否存在头像
        TDUser user = userMapper.selectById(userId);
        // 判断非法用户
        if (user == null) {
            return CommonResp.fail(AppHttpCodeEnum.USER_NOT_EXIST, null);
        }
        if (Common.DELETE.equals(user.getDelFlag()) && Common.STATUS_DISABLE.equals(user.getUserStatus())) {
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        // 有 -> 删除
        if (user.getUserAvatar() != null && !user.getUserAvatar().isEmpty()) {
            File oldFile = new File(uploadPathAvatar + user.getUserAvatar());
            if (!(oldFile.exists() && oldFile.delete())) {
                log.warn("头像删除失败，文件路径为：{}", oldFile.getAbsolutePath());
                return CommonResp.fail(AppHttpCodeEnum.AVATAR_DELETE_ERROR, null);
            }
        }
        String path;
        // 保存头像图片
        String fileName = image.getOriginalFilename();
        String newFileName = System.currentTimeMillis() + "_" + fileName;
        File file = new File(uploadPathAvatar + newFileName);
        try {
            image.transferTo(file.getAbsoluteFile());
            if (!file.exists()) {
                log.error("头像上传失败，文件路径为：{}", file.getAbsolutePath());
                return CommonResp.fail(AppHttpCodeEnum.AVATAR_UPLOAD_ERROR, null);
            }
            // 更新数据库
            path = "/" + Common.getLastPath(uploadPathAvatar, "/", "/" + newFileName);
            user.setUserAvatar(path);
            userMapper.updateById(user);
        } catch (Exception e) {
            log.error("头像上传失败，原因：", e);
            return CommonResp.fail(AppHttpCodeEnum.AVATAR_UPLOAD_ERROR, null);
        }
        return CommonResp.success(Common.getImgURL() + path);
    }

}
