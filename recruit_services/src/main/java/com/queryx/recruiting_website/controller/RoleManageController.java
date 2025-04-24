package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.domain.dto.RoleInfoDto;
import com.queryx.recruiting_website.service.TPRoleService;
import com.queryx.recruiting_website.utils.CommonResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name = "管理员模块下角色管理")
public class RoleManageController {
    @Resource
    private TPRoleService tpRoleService;

    @GetMapping("/selectRoleList")
    @Operation(summary = "查询角色列表")
    public CommonResp selectRoleList() {
        return CommonResp.success(tpRoleService.selectRoleList());
    }

    @PutMapping("/updateRoleStatus/{roleId}/{roleStatus}")
    @Operation(summary = "修改角色状态")
    public CommonResp updateRoleStatus(@PathVariable("roleId") Long roleId,@PathVariable("roleStatus") String roleStatus) {
        return CommonResp.success(tpRoleService.updateRoleStatus(roleId, roleStatus));
    }

    @GetMapping("/role/{roleId}")
    @Operation(summary = "查询角色信息")
    public CommonResp RoleInfo(@PathVariable("roleId") Long roleId) {
        return CommonResp.success(tpRoleService.roleInfo(roleId));
    }


    @PutMapping("/updateRoleInfo")
    @Operation(summary = "更新角色信息")
    public CommonResp updateRoleList(@RequestBody RoleInfoDto roleInfoDto) {
        return CommonResp.success(tpRoleService.updateRoleInfo(roleInfoDto));
    }

    @GetMapping("/selectRoleMenusTree")
    @Operation(summary = "设置角色表单菜单树")
    public CommonResp updateRoleList() {
        return CommonResp.success(tpRoleService.selectRoleMenusTree());
    }

    @DeleteMapping("/delRole/{roleId}")
    @Operation(summary = "删除角色")
    public CommonResp delRole(@PathVariable List<Long> roleId) {
        return CommonResp.success(tpRoleService.delRole(roleId));
    }

    @PostMapping("/addRole")
    @Operation(summary = "添加角色")
    public CommonResp addRole(@RequestBody RoleInfoDto roleInfoDto) {
        return CommonResp.success(tpRoleService.addRole(roleInfoDto));
    }

}
