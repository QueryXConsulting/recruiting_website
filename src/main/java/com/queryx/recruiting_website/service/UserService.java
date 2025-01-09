package com.queryx.recruiting_website.service;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.dto.RegisterDTO;

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
}
