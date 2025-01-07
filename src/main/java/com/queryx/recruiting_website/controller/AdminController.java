package com.queryx.recruiting_website.controller;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.TPMenu;
import com.queryx.recruiting_website.domain.TPRole;
import com.queryx.recruiting_website.domain.dto.*;

import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TPMenuMapper;
import com.queryx.recruiting_website.service.TDAdminService;
import com.queryx.recruiting_website.service.TPMenuService;
import com.queryx.recruiting_website.service.TPRoleService;
import com.queryx.recruiting_website.utils.CommonResp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

@RestController
@RequestMapping("/admin")
@Tag(name = "管理员模块")
public class AdminController {
    @Resource
    private TDAdminService tdAdminService;
    @Resource
    private TPMenuService menuService;


    @PostMapping("/login")
    @Operation(summary = "登录")
    public CommonResp login(@RequestBody AdminLoginDto adminLoginDto) {
        if (!StringUtils.hasText(adminLoginDto.getAdminUsername())) {
            throw new SystemException(AppHttpCodeEnum.PHONE_NULL);
        }
        return CommonResp.success(tdAdminService.login(adminLoginDto));
    }

    @GetMapping("/getInfo")
    @Operation(summary = "拿到管理员信息")
    public CommonResp getInfo() {
        return CommonResp.success(tdAdminService.getInfo());
    }

    @GetMapping("/getRouters")
    @Operation(summary = "拿到管理员菜单数据")
    public CommonResp getRouters() {
        return CommonResp.success(menuService.getRouter());
    }




}
