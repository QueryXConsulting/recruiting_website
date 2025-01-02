package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.*;
import com.queryx.recruiting_website.domain.dto.RoleInfoDto;
import com.queryx.recruiting_website.domain.vo.CompanyInfoDto;
import com.queryx.recruiting_website.domain.vo.RoleListVo;
import com.queryx.recruiting_website.domain.vo.RoleVo;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDCompanyInfoMapper;
import com.queryx.recruiting_website.mapper.TPMenuMapper;
import com.queryx.recruiting_website.mapper.TPRoleMapper;
import com.queryx.recruiting_website.mapper.TPRoleMenuMapper;
import com.queryx.recruiting_website.service.TDCompanyInfoService;
import com.queryx.recruiting_website.service.TPRoleMenuService;
import com.queryx.recruiting_website.service.TPRoleService;
import com.queryx.recruiting_website.utils.CommonResp;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
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
    public List<RoleListVo> selectRoleList() {
        List<TPRole> roleList = list();
        roleList.sort(Comparator.comparing(TPRole::getRoleSort));
        return roleList.stream().map(r -> {
            RoleListVo roleListVo = new RoleListVo();
            BeanUtils.copyProperties(r, roleListVo);
            return roleListVo;
        }).toList();
    }

    @Override
    public RoleInfoDto updateRoleInfo(RoleInfoDto roleInfoDto) {
        TPRole tpRole = new TPRole();
        BeanUtils.copyProperties(roleInfoDto, tpRole);
        UpdateWrapper<TPRole> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("role_id", roleInfoDto.getRoleId());
        roleMapper.update(tpRole, updateWrapper);

        LambdaUpdateWrapper<TPRoleMenu> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TPRoleMenu::getRoleId, roleInfoDto.getRoleId());
        roleMenuMapper.delete(wrapper);

        roleInfoDto.getMenuId().forEach(menuId -> {
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
        UpdateWrapper<TPRole> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("role_id", roleId).set("status", roleStatus);
        if (roleMapper.update(updateWrapper) < 1) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public RoleVo roleInfo(Long roleId) {
        RoleVo roleVo = new RoleVo();
        TPRole role = getById(roleId);
        BeanUtils.copyProperties(role, roleVo);
        LambdaQueryWrapper<TPRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TPRoleMenu::getRoleId,roleId);

        List<TPRoleMenu> tpRoleMenus = roleMenuMapper.selectList(queryWrapper);
        List<Long> menuIdList = tpRoleMenus.stream().map(TPRoleMenu::getMenuId).toList();
        List<String> menuNames = menuMapper.selectByIds(menuIdList).stream().map(TPMenu::getMenuName).toList();
        roleVo.setMenuName(menuNames);
        return roleVo;
    }
}
