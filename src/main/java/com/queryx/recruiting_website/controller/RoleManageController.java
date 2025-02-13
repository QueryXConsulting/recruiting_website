package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.domain.dto.RoleInfoDTO;
import com.queryx.recruiting_website.service.TPRoleService;
import com.queryx.recruiting_website.utils.CommonResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Tag(name = "管理员模块下角色管理")
@PreAuthorize("hasPermission(null ,'system:role:list')")
public class RoleManageController {
    @Resource
    private TPRoleService tpRoleService;

    @GetMapping("/selectRoleList")
    @Operation(summary = "查询角色列表")
    public CommonResp selectRoleList() {
        return CommonResp.success(tpRoleService.selectRoleList());
    }

    @PutMapping("/updateRoleStatus")
    @Operation(summary = "修改角色状态")
    public CommonResp updateRoleStatus(Long roleId, String roleStatus) {
        return CommonResp.success(tpRoleService.updateRoleStatus(roleId, roleStatus));
    }

    @GetMapping("/role/{roleId}")
    @Operation(summary = "查询角色信息")
    @PreAuthorize("hasPermission(null ,'system:role:query')")
    public CommonResp RoleInfo(@PathVariable("roleId") Long roleId) {
        return CommonResp.success(tpRoleService.roleInfo(roleId));
    }

    @PutMapping("/updateRoleInfo")
    @Operation(summary = "更新角色信息")
    @PreAuthorize("hasPermission(null ,'system:role:edit')")
    public CommonResp updateRoleList(@RequestBody RoleInfoDTO roleInfoDTO) {
        return CommonResp.success(tpRoleService.updateRoleInfo(roleInfoDTO));
    }

    @DeleteMapping("/delRole/{roleId}")
    @Operation(summary = "删除角色")
    @PreAuthorize("hasPermission(null ,'system:role:remove')")
    public CommonResp delRole(@PathVariable("roleId") Long roleId) {
        return CommonResp.success(tpRoleService.delRole(roleId));
    }

    @PostMapping("/addRole")
    @Operation(summary = "添加角色")
    @PreAuthorize("hasPermission(null ,'system:role:add')")
    public CommonResp addRole(@RequestBody RoleInfoDTO roleInfoDTO) {
        return CommonResp.success(tpRoleService.addRole(roleInfoDTO));
    }

}
