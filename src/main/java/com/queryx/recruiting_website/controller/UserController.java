package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.dto.RegisterDTO;
import com.queryx.recruiting_website.service.UserService;
import com.queryx.recruiting_website.utils.CommonResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public CommonResp<String> register(@RequestBody RegisterDTO registerDTO) {
        String token = "";
        try {
            token = userService.insertUser(registerDTO);
            // TODO 用户注册：待优化
            switch (token) {
                case "1" -> {
                    return CommonResp.fail(AppHttpCodeEnum.USER_EXIST, null);
                }
                case "2" -> {
                    return CommonResp.fail(AppHttpCodeEnum.PHONE_EXIST, null);
                }
                case "3" -> {
                    return CommonResp.fail(AppHttpCodeEnum.EMAIL_EXIST, null);
                }
                case null -> {
                    return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
                }
                default -> {
                    log.info("用户注册成功");
                    return CommonResp.success(token);
                }
            }
        } catch (Exception e) {
            log.error("用户注册失败, {}", e.getMessage());
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
//        return CommonResp.success(token);
    }

    @PostMapping("/login")
    public CommonResp<String> login(@RequestBody LoginDTO loginDTO) {
        String token = "";
        try {
            token = userService.queryUser(loginDTO);
        } catch (Exception e) {
            log.error("用户登录失败, {}", e.getMessage());
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        log.info("用户登录成功");
        return CommonResp.success(token);
    }

    @PostMapping("/logout")
    public CommonResp<String> logout() {
        // TODO 登出功能待实现
        log.info("logout");
        return null;
    }
}

