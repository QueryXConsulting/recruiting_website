package com.queryx.recruiting_website.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "用户模块", description = "用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     *
     * @param registerDTO 用户注册信息
     * @return 注册结果
     */
    @Operation(summary = "用户注册", parameters = {
            @Parameter(name = "registerDTO", description = "用户注册信息", schema = @Schema(implementation = RegisterDTO.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "注册成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "402", description = "用户已存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "403", description = "邮箱已存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "404", description = "手机号已存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "424", description = "手机号或邮箱输入不合法", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))

    })
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
    @Operation(summary = "用户登录", parameters = {
            @Parameter(name = "loginDTO", description = "用户登录信息", schema = @Schema(implementation = LoginDTO.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "登录成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "414", description = "用户名或密码错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @PostMapping("/login")
    public CommonResp<String> login(@RequestBody LoginDTO loginDTO) {
        String token;
        try {
            token = userService.queryUser(loginDTO);
            if (token == null) {
                return CommonResp.fail(AppHttpCodeEnum.LOGIN_ERROR, null);
            }
        } catch (Exception e) {
            log.error("用户登录失败, 用户输入信息: {}, 错误信息: {}", loginDTO, e.getMessage());
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
    @Operation(summary = "用户登出", description = "用户登出待实现", parameters = {
            @Parameter(name = "token", description = "用户登录token", required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "登出成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @PostMapping("/logout")
    public CommonResp<String> logout() {
        // TODO 登出功能待实现
        log.info("logout");
        return null;
    }
}

