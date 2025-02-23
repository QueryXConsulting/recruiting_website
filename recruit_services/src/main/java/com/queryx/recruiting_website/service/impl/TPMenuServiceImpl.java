package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.LoginAdmin;
import com.queryx.recruiting_website.domain.TPMenu;
import com.queryx.recruiting_website.domain.TPRoleMenu;
import com.queryx.recruiting_website.domain.dto.MenuDto;
import com.queryx.recruiting_website.domain.dto.UpdateMenuDto;
import com.queryx.recruiting_website.domain.vo.MenuListVO;
import com.queryx.recruiting_website.domain.vo.MenuVO;
import com.queryx.recruiting_website.domain.vo.RoutersVO;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TPMenuMapper;
import com.queryx.recruiting_website.mapper.TPRoleMenuMapper;
import com.queryx.recruiting_website.service.TPMenuService;
import com.queryx.recruiting_website.service.TPRoleMenuService;
import com.queryx.recruiting_website.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
    public RoutersVO getRouter() {
        Object principal = SecurityUtils.getAuthentication().getPrincipal();
        RoutersVO routersVO = new RoutersVO();
        Long roleId;
        if (principal instanceof LoginAdmin) {
            roleId = SecurityUtils.getLoginAdmin().getTdAdmin().getRoleId();
        } else {
            roleId = Long.valueOf(SecurityUtils.getLoginUser().getTdUser().getUserRole());
        }
        // menu结果是tree的形式
        List<TPMenu> menus = selectRouterMenuTreeByRoleId(roleId);
        List<MenuVO> menuVOList = menus.stream()
                .map(m -> {
                    MenuVO menuVO = new MenuVO();
                    BeanUtils.copyProperties(m, menuVO);
                    // 子菜单构建
                    menuVO.setChildren(m.getChildren().stream()
                            .map(c -> {
                                MenuVO childMenuVO = new MenuVO();
                                BeanUtils.copyProperties(c, childMenuVO);
                                return childMenuVO;
                            })
                            .collect(Collectors.toList()));
                    return menuVO;
                })
                .toList();
        routersVO.setMenus(menuVOList);
        return routersVO;
    }

    @Override
    public List<MenuListVO> menuList() {
        LambdaQueryWrapper<TPMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TPMenu::getDelFlag, Common.NOT_DELETE)
                .orderByAsc(TPMenu::getParentId, TPMenu::getOrderNum);
        List<TPMenu> list = list(wrapper);

        return buildMenuTreeVO(list, 0L);
    }


    private List<MenuListVO> buildMenuTreeVO(List<TPMenu> menuList, Long parentId) {
        return menuList.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> {
                    MenuListVO menuVO = new MenuListVO();
                    BeanUtils.copyProperties(menu, menuVO);
                    menuVO.setChildren(buildMenuTreeVO(menuList, menu.getMenuId()));
                    return menuVO;
                })
                .collect(Collectors.toList());
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
    public MenuListVO menuInfo(Long menuId) {

        TPMenu menu = getById(menuId);
        if ((menu.getParentId().equals(Common.ROOT_MENU) || menu.getParentId().equals(Long.valueOf(Common.PARENT_MENU)))) {
            throw new SystemException(AppHttpCodeEnum.NO_UPDATE);
        }
        MenuListVO menuListVO = new MenuListVO();
        BeanUtils.copyProperties(menu, menuListVO);
        return menuListVO;
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
        LambdaUpdateWrapper<TPMenu> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TPMenu::getMenuId, menuId)
                .set(TPMenu::getDelFlag, Common.DELETE);
        menuMapper.update(null, wrapper);
        return null;
    }

    public List<TPMenu> selectRouterMenuTreeByRoleId(Long roleId) {
        List<TPMenu> menuList = null;
        LambdaQueryWrapper<TPMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TPMenu::getStatus, Common.STATUS_ENABLE)
                .eq(TPMenu::getDelFlag, Common.NOT_DELETE)
                .in(TPMenu::getMenuType, "C", "M", "U")
                .orderByAsc(TPMenu::getOrderNum);

        // 判断是否是超级管理员
        if (roleId.equals(Common.SUPER_ADMIN)) {
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

    private List<MenuVO> getChildren(TPMenu menu, List<TPMenu> menuList) {
        return menuList.stream()
                .filter(m -> m.getParentId().equals(menu.getMenuId()))
                .map(m -> {
                    MenuVO menuVO = new MenuVO();
                    BeanUtils.copyProperties(m, menuVO);
                    menuVO.setChildren(getChildren(m, menuList)); // 递归获取子菜单
                    return menuVO;
                })
                .collect(Collectors.toList());
    }
}


