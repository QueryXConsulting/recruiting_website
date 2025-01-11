package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TPMenu;
import com.queryx.recruiting_website.domain.TPRoleMenu;
import com.queryx.recruiting_website.domain.dto.MenuDto;
import com.queryx.recruiting_website.domain.dto.UpdateMenuDto;
import com.queryx.recruiting_website.domain.vo.MenuListVo;
import com.queryx.recruiting_website.domain.vo.RoutersVo;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TPMenuMapper;
import com.queryx.recruiting_website.mapper.TPRoleMenuMapper;
import com.queryx.recruiting_website.service.TPMenuService;
import com.queryx.recruiting_website.service.TPRoleMenuService;
import com.queryx.recruiting_website.domain.vo.MenuVo;
import com.queryx.recruiting_website.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service("TPMenuService")
public class TPMenuServiceImpl extends ServiceImpl<TPMenuMapper, TPMenu> implements TPMenuService {

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

    @Override
    public List<MenuListVo> menuList(String status, String menuName) {
        LambdaQueryWrapper<TPMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(menuName != null, TPMenu::getMenuName, menuName)
                .eq(status != null, TPMenu::getStatus, status)
                .eq(TPMenu::getDelFlag,Common.NOT_DELETED.getCode())
                .orderByAsc(TPMenu::getParentId, TPMenu::getOrderNum);
        return list(wrapper).stream().map(menu -> {
            MenuListVo menuListVo = new MenuListVo();
            BeanUtils.copyProperties(menu, menuListVo);
            return menuListVo;
        }).toList();
    }

    @Override
    public Object addMenu(MenuDto menu) {
        TPMenu tpMenu = new TPMenu();
        BeanUtils.copyProperties(menu, tpMenu);
        tpMenu.setCreateBy(SecurityUtils.getLoginAdmin().getTdAdmin().getAdminId());
        tpMenu.setCreateTime(new Date());
        if (!save(tpMenu)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public MenuListVo menuInfo(Long menuId) {

        TPMenu menu = getById(menuId);
        if ((menu.getParentId().equals(Long.valueOf(Common.ROOT_MENU.getCode())) || menu.getParentId().equals(Long.valueOf(Common.PARENT_MENU.getCode())))) {
            throw new SystemException(AppHttpCodeEnum.NO_UPDATE);
        }
        MenuListVo menuListVo = new MenuListVo();
        BeanUtils.copyProperties(menu, menuListVo);
        return menuListVo;
    }

    @Override
    public Object updateMenu(UpdateMenuDto menu) {
        LambdaUpdateWrapper<TPMenu> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TPMenu::getMenuId, menu.getMenuId());
        TPMenu tpMenu = new TPMenu();
        BeanUtils.copyProperties(menu, tpMenu);
        tpMenu.setUpdateBy(SecurityUtils.getLoginAdmin().getTdAdmin().getAdminId());
        tpMenu.setUpdateTime(new Date());

        if (!update(tpMenu, wrapper)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public Object delMenu(Long menuId) {
        TPMenu menu = getById(menuId);
        if ((menu.getParentId().equals(Long.valueOf(Common.ROOT_MENU.getCode())) || menu.getParentId().equals(Long.valueOf(Common.PARENT_MENU.getCode())))) {
            throw new SystemException(AppHttpCodeEnum.NO_UPDATE);
        }

        LambdaUpdateWrapper<TPMenu> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TPMenu::getMenuId, menuId)
                .set(TPMenu::getDelFlag, Common.DELETED.getCode());
        menuMapper.update(null, wrapper);
        return null;
    }

    public List<TPMenu> selectRouterMenuTreeByRoleId(Long roleId) {
        List<TPMenu> menuList = null;
        LambdaQueryWrapper<TPMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TPMenu::getStatus, Common.STATUS_ENABLE.getCode());
        wrapper.in(TPMenu::getMenuType, "C", "M");
        wrapper.orderByAsc(TPMenu::getOrderNum);

        // 判断是否是超级管理员
        if (roleId.equals(Long.valueOf(Common.SUPER_ADMIN.getCode()))) {
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


