package com.queryx.recruiting_website.controller;


import com.queryx.recruiting_website.domain.dto.MenuDTO;
import com.queryx.recruiting_website.domain.dto.UpdateMenuDTO;
import com.queryx.recruiting_website.service.TPMenuService;
import com.queryx.recruiting_website.utils.CommonResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Tag(name = "管理员模块下菜单管理")
@PreAuthorize("hasPermission(null ,'system:menu:list')")
public class MenuMangeController {

    @Resource
    private TPMenuService menuService;

    @GetMapping("/menuList")
    @Operation(summary = "菜单列表")
    public CommonResp menuList(String status, String menuName) {
        return CommonResp.success(menuService.menuList(status, menuName));
    }

    @PostMapping("/addMenu")
    @Operation(summary = "增加菜单")
    @PreAuthorize("hasPermission(null ,'system:menu:add')")
    public CommonResp addMenu(@RequestBody MenuDTO menu) {
        return CommonResp.success(menuService.addMenu(menu));
    }

    @GetMapping("/menuInfo/{menuId}")
    @Operation(summary = "菜单数据查询")
    @PreAuthorize("hasPermission(null ,'system:menu:query')")
    public CommonResp queryMenuInfo(@PathVariable("menuId") Long menuId) {
        return CommonResp.success(menuService.menuInfo(menuId));
    }

    @PutMapping("/updateMenu")
    @Operation(summary = "菜单数据更新")
    @PreAuthorize("hasPermission(null ,'system:menu:edit')")
    public CommonResp updateMenu(@RequestBody UpdateMenuDTO menu) {
        return CommonResp.success(menuService.updateMenu(menu));
    }

    @DeleteMapping("/delMenu/{menuId}")
    @Operation(summary = "菜单数据删除")
    @PreAuthorize("hasPermission(null ,'system:menu:remove')")
    public CommonResp delMenu(@PathVariable("menuId") Long menuId) {
        return CommonResp.success(menuService.delMenu(menuId));
    }

}
