package com.queryx.recruiting_website.controller;

import lombok.extern.slf4j.Slf4j;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.queryx.recruiting_website.domain.dto.RegisterDTO;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     *
     * @param registerDTO 用户注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public CommonResp<String> register(@RequestBody RegisterDTO registerDTO) {
        AppHttpCodeEnum code;
        try {
            code = userService.insertUser(registerDTO);
            if (code == null) {
                throw new Exception("注册返回为null");
            }
            switch (code) {
                case USER_EXIST:
                case EMAIL_EXIST:
                case PHONE_EXIST:
                case PHONE_OR_EMAIL_ILLEGAL: {
                    return CommonResp.fail(code, null);
                }
            }
        } catch (Exception e) {
            log.error("用户注册失败, {}", e.getMessage());
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        return CommonResp.success(null);
    }

    /**
     * 用户登录
     *
     * @param loginDTO 用户登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public CommonResp<String> login(@RequestBody LoginDTO loginDTO) {
        String token;
        try {
            token = userService.queryUser(loginDTO);
        } catch (Exception e) {
            log.error("用户登录失败, {}", e.getMessage());
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        log.info("用户登录成功");
        return CommonResp.success(token);
    }

    /**
     * 用户登出
     *
     * @return 登出结果
     */
    @PostMapping("/logout")
    public CommonResp<String> logout() {
        // TODO 登出功能待实现
        log.info("logout");
        return null;
    }
}

