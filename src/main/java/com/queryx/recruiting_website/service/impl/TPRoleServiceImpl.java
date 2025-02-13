package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.*;
import com.queryx.recruiting_website.domain.dto.RoleInfoDTO;
import com.queryx.recruiting_website.domain.vo.RoleListVO;
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

import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
        List<TPRole> roleList = list();
        roleList.sort(Comparator.comparing(TPRole::getRoleSort));
        return roleList.stream().map(r -> {
            RoleListVO roleListVO = new RoleListVO();
            BeanUtils.copyProperties(r, roleListVO);
            return roleListVO;
        }).toList();
    }

    @Override
    public RoleInfoDTO updateRoleInfo(RoleInfoDTO roleInfoDTO) {
        if (roleInfoDTO.getRoleId().equals(Common.SUPER_ADMIN)){
            throw new SystemException(AppHttpCodeEnum.SUPER_ADMIN);
        }
        TPRole tpRole = new TPRole();
        BeanUtils.copyProperties(roleInfoDTO, tpRole);
        UpdateWrapper<TPRole> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("role_id", roleInfoDTO.getRoleId());
        tpRole.setUpdateBy(SecurityUtils.getLoginAdmin().getTdAdmin().getAdminId());
        tpRole.setUpdateTime(new Date());
        roleMapper.update(tpRole, updateWrapper);

        LambdaUpdateWrapper<TPRoleMenu> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TPRoleMenu::getRoleId, roleInfoDTO.getRoleId());
        roleMenuMapper.delete(wrapper);

        roleInfoDTO.getMenuId().forEach(menuId -> {
            TPRoleMenu tpRoleMenu = new TPRoleMenu();
            tpRoleMenu.setRoleId(roleInfoDTO.getRoleId());
            tpRoleMenu.setMenuId(menuId);
            roleMenuMapper.
                    insert(tpRoleMenu);
        });
        return null;
    }

    @Override
    public String updateRoleStatus(Long roleId, String roleStatus) {
        if (roleId.equals(Common.SUPER_ADMIN)){
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
        LambdaQueryWrapper<TPRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TPRoleMenu::getRoleId, roleId);

        List<TPRoleMenu> tpRoleMenus = roleMenuMapper.selectList(queryWrapper);
        List<Long> menuIdList = tpRoleMenus.stream().map(TPRoleMenu::getMenuId).toList();
        List<String> menuNames = menuMapper.selectByIds(menuIdList).stream().map(TPMenu::getMenuName).toList();
        roleVO.setMenuName(menuNames);
        return roleVO;
    }

    @Override
    public Object addRole(RoleInfoDTO roleInfoDTO) {
        TPRole tpRole = new TPRole();
        BeanUtils.copyProperties(roleInfoDTO, tpRole);
        tpRole.setCreateTime(new Date());
        tpRole.setStatus(Common.STATUS_ENABLE);
        tpRole.setCreateTime(new Date());
        save(tpRole);
        roleInfoDTO.setRoleId(tpRole.getRoleId());
        roleInfoDTO.getMenuId().forEach(menuId -> {
            TPRoleMenu tpRoleMenu = new TPRoleMenu();
            tpRoleMenu.setRoleId(roleInfoDTO.getRoleId());
            tpRoleMenu.setMenuId(menuId);
            roleMenuMapper.
                    insert(tpRoleMenu);
        });
        return null;
    }

    @Override
    public Object delRole(Long roleId) {
        if (roleId.equals(Common.SUPER_ADMIN)){
            throw new SystemException(AppHttpCodeEnum.SUPER_ADMIN);
        }
        LambdaUpdateWrapper<TPRole> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TPRole::getRoleId,roleId)
                .set(TPRole::getDelFlag,Common.DELETE);
        update(null,updateWrapper);

        return null;
    }
}
