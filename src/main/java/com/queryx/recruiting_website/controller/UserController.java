package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.dto.UserRegisterDTO;
import com.queryx.recruiting_website.service.TDUserService;
import com.queryx.recruiting_website.utils.CommonResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "用户模块")
public class UserController {
   @Resource
   private TDUserService tdUserService;

    @PostMapping("/login")
    @Operation(summary = "登录功能", description = "公司用户以及学生用户公用")
    public CommonResp login(@RequestBody LoginDTO loginDTO) {
        return CommonResp.success(tdUserService.login(loginDTO));
    }
    @PostMapping("/register")
    @Operation(summary = "注册功能", description = "公司用户以及学生用户公用")
    public CommonResp register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return CommonResp.success(tdUserService.register(userRegisterDTO));
    }
}
