package com.queryx.recruiting_website.controller;


import com.queryx.recruiting_website.domain.dto.MenuDto;
import com.queryx.recruiting_website.domain.dto.UpdateMenuDto;
import com.queryx.recruiting_website.service.TPMenuService;
import com.queryx.recruiting_website.utils.CommonResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Tag(name = "管理员模块下菜单管理")
public class MenuMangeController {

    @Resource
    private TPMenuService menuService;

    @GetMapping("/menuList")
    @Operation(summary = "菜单列表")
    public CommonResp menuList() {
        return CommonResp.success(menuService.menuList());
    }

    @PostMapping("/addMenu")
    @Operation(summary = "增加菜单")
    public CommonResp addMenu(@RequestBody MenuDto menu) {
        return CommonResp.success(menuService.addMenu(menu));
    }

    @GetMapping("/menuInfo/{menuId}")
    @Operation(summary = "菜单数据查询")
    public CommonResp queryMenuInfo(@PathVariable("menuId") Long menuId) {
        return CommonResp.success(menuService.menuInfo(menuId));
    }

    @PutMapping("/updateMenu")
    @Operation(summary = "菜单数据更新")
    public CommonResp updateMenu(@RequestBody UpdateMenuDto menu) {
        return CommonResp.success(menuService.updateMenu(menu));
    }

    @DeleteMapping("/delMenu/{menuId}")
    @Operation(summary = "菜单数据删除")
    public CommonResp delMenu(@PathVariable("menuId") Long menuId) {
        return CommonResp.success(menuService.delMenu(menuId));
    }

}
