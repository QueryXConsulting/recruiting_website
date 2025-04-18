package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDResume;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.dto.RegisterDTO;
import com.queryx.recruiting_website.mapper.TDResumeMapper;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import com.queryx.recruiting_website.service.UserService;
import com.queryx.recruiting_website.utils.CommonResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private TDResumeMapper resumeMapper;

    @Value("${file.upload-path-avatar}")
    private String uploadPathAvatar;

    public static final String PHONE = "(^1[3-9]\\d{9}$)";
    public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

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
        BeanUtils.copyProperties(registerDTO, user);
        BeanUtils.copyProperties(registerDTO, userResume);
        user.setUserRegisterTime(Date.from(ZonedDateTime.now(ZoneId.of(timeZone)).toInstant()));
        // 设置默认值
        userResume.setResumeName(registerDTO.getUserName());
        user.setUserPhone(registerDTO.getResumePhone());
        user.setUserEmail(registerDTO.getResumeEmail());
        // 插入用户
        resumeMapper.insert(userResume);
        userMapper.insert(user);
        // 更新用户简历ID（因为新用户没有简历，简历id为null，插入简历后，获取用户简历ID）
        user.setResumeId(userResume.getResumeId());
        userMapper.updateById(user);
        return AppHttpCodeEnum.SUCCESS;
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
