package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.domain.dto.AdminDto;
import com.queryx.recruiting_website.domain.dto.UserDto;
import com.queryx.recruiting_website.service.TDAdminService;
import com.queryx.recruiting_website.service.TDUserService;
import com.queryx.recruiting_website.utils.CommonResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasPermission(null ,'system:user:list')")
@Tag(name = "管理员模块下用户相关数据管理")
@RequestMapping("/admin")
public class UserManageController {
    @Resource
    private TDUserService userService;
    @Resource
    private TDAdminService adminService;

    @PostMapping("/addAdmin")
    @Operation(summary = "添加管理员")
    @PreAuthorize("hasPermission(null ,'system:user:add')")
    public CommonResp addAdmin(@RequestBody AdminDto adminDto) {
        return CommonResp.success(adminService.addAdmin(adminDto));
    }

    @GetMapping("/selectAdminList")
    @Operation(summary = "管理员列表")
    public CommonResp selectAdminList(Integer page, Integer size) {
        return CommonResp.success(adminService.selectAdminList(page, size));
    }

    @GetMapping("/selectAdminInfo/{adminId}")
    @Operation(summary = "管理员信息")
    @PreAuthorize("hasPermission(null ,'system:user:query')")
    public CommonResp selectAdminInfo(@PathVariable("adminId") Long adminId) {
        return CommonResp.success(adminService.selectAdminInfo(adminId));
    }

    @PutMapping("/updateAdminInfo")
    @Operation(summary = "管理员数据更新")
    @PreAuthorize("hasPermission(null ,'system:user:edit')")
    public CommonResp updateAdminInfo(@RequestBody AdminDto adminDto) {
        return CommonResp.success(adminService.updateAdminInfo(adminDto));
    }

    @DeleteMapping("/deleteAdmin/{adminId}")
    @Operation(summary = "管理员账号删除")
    @PreAuthorize("hasPermission(null ,'system:user:remove')")
    public CommonResp deleteAdmin(@PathVariable("adminId") Long adminId) {
        return CommonResp.success(adminService.deleteAdmin(adminId));
    }

    @GetMapping("/selectUserList")
    @Operation(summary = "用户列表")
    public CommonResp selectUserList(Integer page, Integer size) {
        return CommonResp.success(userService.selectUserList(page, size));
    }


    @GetMapping("/selectUserInfo/{userId}")
    @Operation(summary = "用户信息")
    @PreAuthorize("hasPermission(null ,'system:user:query')")
    public CommonResp selectUserbInfo(@PathVariable("userId") Long userId) {
        return CommonResp.success(userService.selectUserInfo(userId, null));
    }



    @PutMapping("/updateUserInfo")
    @Operation(summary = "用户数据更新")
    @PreAuthorize("hasPermission(null ,'system:user:edit')")
    public CommonResp updateUserInfo(@RequestBody UserDto userDto) {
        return CommonResp.success(userService.updateUserInfo(userDto));
    }

    @PostMapping("/addUser")
    @Operation(summary = "添加用户")
    @PreAuthorize("hasPermission(null ,'system:user:add')")
    public CommonResp addUser(@RequestBody UserDto userDto) {
        return CommonResp.success(userService.addUser(userDto));
    }


    @DeleteMapping("/deleteUser/{userId}")
    @Operation(summary = "用户账号删除")
    @PreAuthorize("hasPermission(null ,'system:user:remove')")
    public CommonResp deleteUser(@PathVariable("userId") Long userId) {
        return CommonResp.success(userService.deleteUser(userId));
    }

}
