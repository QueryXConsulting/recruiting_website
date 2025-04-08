package com.queryx.recruiting_website.controller;


import com.queryx.recruiting_website.domain.dto.AdminLoginDto;
import com.queryx.recruiting_website.service.IOperateLogService;
import com.queryx.recruiting_website.service.TDAdminService;
import com.queryx.recruiting_website.utils.CommonResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Tag(name = "管理员模块")
public class AdminController {
    @Resource
    private TDAdminService tdAdminService;
    @Resource
    private IOperateLogService iOperateLogService;


    @PostMapping("/login")
    @Operation(summary = "登录")
    public CommonResp login(@RequestBody AdminLoginDto adminLoginDto) {
        return CommonResp.success(tdAdminService.login(adminLoginDto));
    }

    @GetMapping("/getInfo")
    @Operation(summary = "拿到管理员信息")
    public CommonResp getInfo() {
        return CommonResp.success(tdAdminService.getInfo());
    }

    @GetMapping("/getLog")
    @Operation(summary = "日志信息")
    public CommonResp getLog(Integer page,Integer size,String operateName,String startTime,String endTime) {
        return CommonResp.success(iOperateLogService.getLog(page,size,operateName,startTime,endTime));
    }


}
