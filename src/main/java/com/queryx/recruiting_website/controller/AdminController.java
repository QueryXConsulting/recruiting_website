package com.queryx.recruiting_website.controller;


import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.service.TDAdminService;
import com.queryx.recruiting_website.service.TPMenuService;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.domain.dto.AdminLoginDto;

import com.queryx.recruiting_website.domain.dto.AdminDto;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private TDAdminService tdAdminService;
    @Resource
    private TPMenuService menuService;


    @PostMapping("/addAdmin")
    public CommonResp addAdmin(@RequestBody AdminDto adminDto) {
        return CommonResp.success(tdAdminService.addAdmin(adminDto));
    }

    @PostMapping("/login")
    public CommonResp login(@RequestBody AdminLoginDto adminLoginDto) {
        if (!StringUtils.hasText(adminLoginDto.getAdminUsername())) {
            throw new SystemException(AppHttpCodeEnum.PHONE_NULL);
        }
        return CommonResp.success(tdAdminService.login(adminLoginDto));
    }

    @GetMapping("/getInfo")
    public CommonResp getInfo() {
        return CommonResp.success(tdAdminService.getInfo());
    }

    @GetMapping("/getRouters")
    public CommonResp getRouters() {
        return CommonResp.success(menuService.getRouter());
    }


}
