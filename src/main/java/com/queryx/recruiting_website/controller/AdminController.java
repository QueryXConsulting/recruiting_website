package com.queryx.recruiting_website.controller;


import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.service.TDAdminService;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.vo.AdminLoginVo;

import com.queryx.recruiting_website.vo.AdminVo;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private TDAdminService tdAdminService;

    @PostMapping("/addAdmin")
    public CommonResp addAdmin(@RequestBody AdminVo adminVo){
        return CommonResp.success(tdAdminService.addAdmin(adminVo));
    }

    @PostMapping("/login")
    public CommonResp login(@RequestBody AdminLoginVo adminLoginVo){
        if (!StringUtils.hasText(adminLoginVo.getAdminUsername())) {
            throw new SystemException(AppHttpCodeEnum.PHONE_NULL);
        }
        return CommonResp.success(tdAdminService.login(adminLoginVo));
    }


}
