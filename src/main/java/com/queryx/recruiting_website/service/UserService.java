package com.queryx.recruiting_website.service;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.dto.RegisterDTO;
import com.queryx.recruiting_website.utils.CommonResp;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    /**
     * 用户注册
     *
     * @param registerDTO 用户注册信息
     * @return 注册结果
     */
    AppHttpCodeEnum insertUser(RegisterDTO registerDTO);

    /**
     * 用户登录
     *
     * @param loginDTO 用户登录信息
     * @return token
     */
    String queryUser(LoginDTO loginDTO);

    /**
     * 用户更新信息
     *
     * @param userId 用户id
     * @param image  用户头像
     * @return 更新结果
     */
    CommonResp<String> uploadAvatar(String userId, MultipartFile image);
}
