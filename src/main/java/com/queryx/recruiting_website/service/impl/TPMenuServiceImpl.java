package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.domain.TPMenu;
import com.queryx.recruiting_website.domain.TPRoleMenu;
import com.queryx.recruiting_website.domain.vo.RoutersVo;
import com.queryx.recruiting_website.mapper.TPMenuMapper;
import com.queryx.recruiting_website.mapper.TPRoleMenuMapper;
import com.queryx.recruiting_website.service.TPMenuService;
import com.queryx.recruiting_website.service.TPRoleMenuService;
import com.queryx.recruiting_website.domain.vo.MenuVo;
import com.queryx.recruiting_website.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service("TPMenuService")
public class TPMenuServiceImpl extends ServiceImpl<TPMenuMapper, TPMenu> implements TPMenuService {
    private static final Long SUPER_ADMIN = 1L;
    @Resource
    private TPMenuMapper menuMapper;
    @Resource
    private TPRoleMenuService roleMenuService;
    @Resource
    private TPRoleMenuMapper roleMenuMapper;

    @Override
    public RoutersVo getRouter() {
        // menu结果是tree的形式
        List<TPMenu> menus = selectRouterMenuTreeByRoleId(SecurityUtils.getLoginAdmin().getTdAdmin().getRoleId());
        RoutersVo routersVo = new RoutersVo();
        List<MenuVo> menuVoList = menus.stream()
                .map(m -> {
                    MenuVo menuVo = new MenuVo();
                    BeanUtils.copyProperties(m, menuVo);
                    // 子菜单构建
                    menuVo.setChildren(m.getChildren().stream()
                            .map(c -> {
                                MenuVo childMenuVo = new MenuVo();
                                BeanUtils.copyProperties(c, childMenuVo);
                                return childMenuVo;
                            })
                            .collect(Collectors.toList()));
                    return menuVo;
                })
                .collect(Collectors.toList());
        routersVo.setMenus(menuVoList);
        return routersVo;
    }

    public List<TPMenu> selectRouterMenuTreeByRoleId(Long roleId) {
        List<TPMenu> menuList = null;
        LambdaQueryWrapper<TPMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TPMenu::getStatus, "0");
        wrapper.in(TPMenu::getMenuType, "C", "M");
        wrapper.orderByAsc(TPMenu::getOrderNum);

        // 判断是否是超级管理员
        if (roleId.equals(SUPER_ADMIN)) {
            menuList = menuMapper.selectList(wrapper);
        } else {
            LambdaQueryWrapper<TPRoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
            roleMenuWrapper.eq(TPRoleMenu::getRoleId, roleId);
            List<TPRoleMenu> menus = roleMenuMapper.selectList(roleMenuWrapper);
            List<Long> menuIds = menus.stream().map(TPRoleMenu::getMenuId).collect(Collectors.toList());
            wrapper.in(TPMenu::getMenuId, menuIds);
            menuList = list(wrapper);
        }

        // 得到树形结构
        List<TPMenu> menuTree = builderMenuTree(menuList, 0L);
        return menuTree;
    }

    private List<TPMenu> builderMenuTree(List<TPMenu> menuList, Long parentId) {
        return menuList.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .peek(menu -> menu.setChildren(getChildren(menu, menuList)))
                .collect(Collectors.toList());
    }

    private List<MenuVo> getChildren(TPMenu menu, List<TPMenu> menuList) {
        return menuList.stream()
                .filter(m -> m.getParentId().equals(menu.getMenuId()))
                .map(m -> {
                    MenuVo menuVo = new MenuVo();
                    BeanUtils.copyProperties(m, menuVo);
                    menuVo.setChildren(getChildren(m, menuList)); // 递归获取子菜单
                    return menuVo;
                })
                .collect(Collectors.toList());
    }
}


