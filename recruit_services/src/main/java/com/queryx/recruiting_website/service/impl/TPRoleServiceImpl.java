package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.LoginUser;
import com.queryx.recruiting_website.domain.TPMenu;
import com.queryx.recruiting_website.domain.TPRole;
import com.queryx.recruiting_website.domain.TPRoleMenu;
import com.queryx.recruiting_website.domain.dto.RoleInfoDto;
import com.queryx.recruiting_website.domain.vo.RoleListVO;
import com.queryx.recruiting_website.domain.vo.RoleMenuVO;
import com.queryx.recruiting_website.domain.vo.RoleVO;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TPMenuMapper;
import com.queryx.recruiting_website.mapper.TPRoleMapper;
import com.queryx.recruiting_website.mapper.TPRoleMenuMapper;
import com.queryx.recruiting_website.service.TPRoleService;
import com.queryx.recruiting_website.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TPRoleServiceImpl extends ServiceImpl<TPRoleMapper, TPRole> implements TPRoleService {

    @Resource
    private TPRoleMapper roleMapper;
    @Resource
    private TPRoleMenuMapper roleMenuMapper;
    @Resource
    private TPMenuMapper menuMapper;

    @Override
    public List<RoleListVO> selectRoleList() {
        Object principal = SecurityUtils.getAuthentication().getPrincipal();
        List<TPRole> roleList = list( new LambdaUpdateWrapper<TPRole>()
                .eq(TPRole::getDelFlag,Common.NOT_DELETE)
                .eq(principal instanceof LoginUser,TPRole::getStatus,Common.STATUS_ENABLE)
                .eq(principal instanceof LoginUser,TPRole::getRoleType,'0')
        );
        roleList.sort(Comparator.comparing(TPRole::getRoleSort));
        return roleList.stream().map(r -> {
            RoleListVO roleListVO = new RoleListVO();
            BeanUtils.copyProperties(r, roleListVO);
            return roleListVO;
        }).toList();
    }

    @Override
    public RoleInfoDto updateRoleInfo(RoleInfoDto roleInfoDto) {
        if (roleInfoDto.getRoleId().equals(Common.SUPER_ADMIN)) {
            throw new SystemException(AppHttpCodeEnum.SUPER_ADMIN);
        }
        TPRole tpRole = new TPRole();
        BeanUtils.copyProperties(roleInfoDto, tpRole);
        UpdateWrapper<TPRole> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("role_id", roleInfoDto.getRoleId());
        tpRole.setUpdateBy(SecurityUtils.getLoginAdmin().getTdAdmin().getAdminId());
        tpRole.setUpdateTime(new Date());
        roleMapper.update(tpRole, updateWrapper);

        LambdaUpdateWrapper<TPRoleMenu> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TPRoleMenu::getRoleId, roleInfoDto.getRoleId());
        roleMenuMapper.delete(wrapper);

        roleInfoDto.getMenuIds().forEach(menuId -> {
            TPRoleMenu tpRoleMenu = new TPRoleMenu();
            tpRoleMenu.setRoleId(roleInfoDto.getRoleId());
            tpRoleMenu.setMenuId(menuId);
            roleMenuMapper.
                    insert(tpRoleMenu);
        });
        return null;
    }

    @Override
    public String updateRoleStatus(Long roleId, String roleStatus) {
        if (roleId.equals(Common.SUPER_ADMIN)) {
            throw new SystemException(AppHttpCodeEnum.SUPER_ADMIN);
        }

        UpdateWrapper<TPRole> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("role_id", roleId).set("status", roleStatus);
        if (roleMapper.update(updateWrapper) < 1) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public RoleVO roleInfo(Long roleId) {
        RoleVO roleVO = new RoleVO();
        TPRole role = getById(roleId);
        BeanUtils.copyProperties(role, roleVO);

        if (roleId.equals(Common.SUPER_ADMIN)) {
            List<TPMenu> tpMenus = menuMapper.selectList(null);
            roleVO.setMenusListId(tpMenus.stream().map(TPMenu::getMenuId).map(String::valueOf).toList());
            return roleVO;
        }
        LambdaQueryWrapper<TPRoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
        roleMenuWrapper.eq(!ObjectUtils.isEmpty(roleId), TPRoleMenu::getRoleId, roleId);
        List<TPRoleMenu> menus = roleMenuMapper.selectList(roleMenuWrapper);
        List<String> menuIdStrings = menus.stream()
                .map(TPRoleMenu::getMenuId)
                .map(String::valueOf)
                .toList();
        roleVO.setMenusListId(menuIdStrings);
        return roleVO;
    }

    @Override
    public List<RoleMenuVO> selectRoleMenusTree() {
        LambdaQueryWrapper<TPMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TPMenu::getStatus, Common.STATUS_ENABLE);
        wrapper.eq(TPMenu::getDelFlag,Common.NOT_DELETE);
        wrapper.orderByAsc(TPMenu::getOrderNum);

        return buildMenuTreeVO(menuMapper.selectList(wrapper), 0L);
    }


    private List<RoleMenuVO> buildMenuTreeVO(List<TPMenu> menuList, Long parentId) {
        return menuList.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> {
                    RoleMenuVO roleMenuVO = new RoleMenuVO();
                    BeanUtils.copyProperties(menu, roleMenuVO);
                    roleMenuVO.setChildren(buildMenuTreeVO(menuList, menu.getMenuId()));
                    return roleMenuVO;
                })
                .collect(Collectors.toList());
    }


    @Override
    public Object addRole(RoleInfoDto roleInfoDto) {
        TPRole tpRole = new TPRole();
        BeanUtils.copyProperties(roleInfoDto, tpRole);
        tpRole.setCreateTime(new Date());
        tpRole.setStatus(Common.STATUS_ENABLE);
        tpRole.setCreateTime(new Date());
        save(tpRole);
        roleInfoDto.setRoleId(tpRole.getRoleId());
        roleInfoDto.getMenuIds().forEach(menuId -> {
            TPRoleMenu tpRoleMenu = new TPRoleMenu();
            tpRoleMenu.setRoleId(roleInfoDto.getRoleId());
            tpRoleMenu.setMenuId(menuId);
            roleMenuMapper.
                    insert(tpRoleMenu);
        });
        return null;
    }

    @Override
    public Object delRole(List<Long> roleId) {
        if (roleId.contains(Common.SUPER_ADMIN)) {
            throw new SystemException(AppHttpCodeEnum.SUPER_ADMIN);
        }
        LambdaUpdateWrapper<TPRole> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(TPRole::getRoleId, roleId)
                .set(TPRole::getDelFlag, Common.DELETE);
        update(null, updateWrapper);

        return null;
    }
}
