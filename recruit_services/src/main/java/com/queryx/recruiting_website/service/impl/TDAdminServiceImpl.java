package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.LoginAdmin;
import com.queryx.recruiting_website.domain.TDAdmin;
import com.queryx.recruiting_website.domain.TPMenu;
import com.queryx.recruiting_website.domain.TPRole;
import com.queryx.recruiting_website.domain.dto.AdminDto;
import com.queryx.recruiting_website.domain.dto.AdminLoginDto;
import com.queryx.recruiting_website.domain.vo.AdminInfoVO;
import com.queryx.recruiting_website.domain.vo.AdminUserInfoVO;
import com.queryx.recruiting_website.domain.vo.AdminVO;
import com.queryx.recruiting_website.domain.vo.UserLoginVO;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDAdminMapper;
import com.queryx.recruiting_website.mapper.TPMenuMapper;
import com.queryx.recruiting_website.mapper.TPRoleMapper;
import com.queryx.recruiting_website.service.TDAdminService;
import com.queryx.recruiting_website.utils.JwtUtil;
import com.queryx.recruiting_website.utils.SecurityUtils;
import com.queryx.recruiting_website.utils.TokenStorage;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service("tDAdminService")
public class TDAdminServiceImpl extends ServiceImpl<TDAdminMapper, TDAdmin> implements TDAdminService {

    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private TDAdminMapper tdAdminMapper;
    @Resource
    private TPMenuMapper menuMapper;
    @Resource
    private TPRoleMapper roleMapper;

    @Override
    public AdminDto addAdmin(AdminDto adminDto) {
        if (!StringUtils.hasText(adminDto.getAdminUsername())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }

        if (!StringUtils.hasText(adminDto.getAdminPassword())) {
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }

        LambdaQueryWrapper<TDAdmin> tdAdminLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tdAdminLambdaQueryWrapper.eq(TDAdmin::getAdminUsername, adminDto.getAdminUsername());
        if (count(tdAdminLambdaQueryWrapper) > 0) {
            throw new SystemException(AppHttpCodeEnum.USER_EXIST);
        }

        String password = passwordEncoder.encode(adminDto.getAdminPassword());
        adminDto.setAdminPassword(password);
        TDAdmin tdAdmin = new TDAdmin();
        BeanUtils.copyProperties(adminDto, tdAdmin);
        tdAdmin.setAdminStatus(Common.STATUS_ENABLE);
        tdAdmin.setAdminCreateTime(new Date());
        if (!save(tdAdmin)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }

        return null;
    }

    @Override
    public UserLoginVO login(AdminLoginDto adminLoginDto) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(adminLoginDto.getUsername(), adminLoginDto.getUserPassword());
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        // 判断是否验证通过
        if (Objects.isNull(authenticate)) {
            throw new SystemException(AppHttpCodeEnum.LOGIN_ERROR);
        }
        LoginAdmin loginAdmin = (LoginAdmin) authenticate.getPrincipal();
        UserLoginVO userLoginVO = new UserLoginVO();
        String jwt = JwtUtil.createJWT(loginAdmin.getTdAdmin().getAdminId());
        userLoginVO.setToken(jwt);
        TokenStorage.addToken(jwt, loginAdmin);
        // 返回前端凭证
        return userLoginVO;
    }

    @Override
    public AdminUserInfoVO getInfo() {

        LoginAdmin loginAdmin = SecurityUtils.getLoginAdmin();
        TDAdmin tdAdmin = loginAdmin.getTdAdmin();
        List<String> perms = selectPermsByRoleId(tdAdmin.getRoleId());

        AdminInfoVO adminInfoVO = new AdminInfoVO();
        BeanUtils.copyProperties(tdAdmin, adminInfoVO);
        AdminUserInfoVO adminUserInfoVO = new AdminUserInfoVO();
        adminUserInfoVO.setPermissions(perms);
        adminUserInfoVO.setUser(adminInfoVO);
        adminInfoVO.setAdminAvatar(Common.getImgURL() + loginAdmin.getTdAdmin().getAdminAvatar());
        return adminUserInfoVO;
    }

    @Override
    public Page<AdminVO> selectAdminList(Integer page, Integer size, String adminName, String adminStatus) {
        LambdaUpdateWrapper<TDAdmin> tdAdminLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        tdAdminLambdaUpdateWrapper.like(adminName != null, TDAdmin::getAdminName, adminName)
                .eq(adminStatus != null, TDAdmin::getAdminStatus, adminStatus);
        Page<TDAdmin> tdUserPage = tdAdminMapper.selectPage(new Page<>(page, size), tdAdminLambdaUpdateWrapper);
        Page<AdminVO> adminVOPage = new Page<>(tdUserPage.getCurrent(), tdUserPage.getSize(), tdUserPage.getTotal());

        List<Long> roleIds = tdUserPage.getRecords().stream().map(TDAdmin::getRoleId).toList();
        List<TPRole> tpRoles = roleMapper.selectByIds(roleIds);
        Map<Long, String> roleName = tpRoles.stream().collect(Collectors.toMap(TPRole::getRoleId, TPRole::getRoleName));
        adminVOPage.setRecords(tdUserPage.getRecords().stream().map(user -> {
            AdminVO adminVO = new AdminVO();
            BeanUtils.copyProperties(user, adminVO);
            adminVO.setRoleName(roleName.get(user.getRoleId()));
            adminVO.setAdminAvatar(Common.getImgURL() + user.getAdminAvatar());
            return adminVO;
        }).collect(Collectors.toList()));

        return adminVOPage;
    }

    @Override
    public AdminVO selectAdminInfo(Long userId) {
        TDAdmin byId = getById(userId);
        TPRole tpRole = roleMapper.selectById(byId.getRoleId());
        AdminVO adminVO = new AdminVO();
        BeanUtils.copyProperties(byId, adminVO);
        adminVO.setRoleName(tpRole.getRoleName());
        adminVO.setAdminAvatar(Common.getImgURL() + byId.getAdminAvatar());
        return adminVO;
    }

    @Override
    public Object updateAdminInfo(AdminDto adminDto) {
        TDAdmin tdAdmin = new TDAdmin();
        BeanUtils.copyProperties(adminDto, tdAdmin);
        tdAdmin.setAdminPassword(passwordEncoder.encode(tdAdmin.getAdminPassword()));
        if (!update(tdAdmin, new LambdaUpdateWrapper<TDAdmin>().eq(TDAdmin::getAdminId, tdAdmin.getAdminId()))) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }

        return null;
    }

    @Override
    public Object deleteAdmin(Long adminId) {

        if (tdAdminMapper.deleteById(adminId) < 1) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    public List<String> selectPermsByRoleId(Long roleId) {
        LambdaQueryWrapper<TPMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(TPMenu::getMenuType, "F", "U");
        wrapper.eq(TPMenu::getStatus, Common.STATUS_ENABLE);
        // 如果是超级管理员返回所有权限
        if (roleId.equals(Common.SUPER_ADMIN)) {
            List<TPMenu> menus = menuMapper.selectList(wrapper);
            List<String> perms = menus.stream().map(TPMenu::getPerms).collect(Collectors.toList());
            return perms;
        }

        return menuMapper.selectPermsByRoleId(roleId);
    }


}


