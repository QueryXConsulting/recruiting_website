package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.dto.UserRegisterDTO;
import com.queryx.recruiting_website.service.TDUserService;
import com.queryx.recruiting_website.utils.CommonResp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
   @Resource
   private TDUserService tdUserService;

    @PostMapping("/login")
    public CommonResp login(@RequestBody LoginDTO loginDTO) {
        return CommonResp.success(tdUserService.login(loginDTO));
    }
    @PostMapping("/register")
    public CommonResp register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return CommonResp.success(tdUserService.register(userRegisterDTO));
    }
}
