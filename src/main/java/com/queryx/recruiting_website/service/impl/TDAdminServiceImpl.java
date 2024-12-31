package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.LoginAdmin;
import com.queryx.recruiting_website.domain.TDAdmin;

import com.queryx.recruiting_website.domain.TPMenu;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDAdminMapper;

import com.queryx.recruiting_website.mapper.TPMenuMapper;
import com.queryx.recruiting_website.service.TDAdminService;
import com.queryx.recruiting_website.domain.dto.AdminLoginDto;

import com.queryx.recruiting_website.domain.dto.AdminDto;
import com.queryx.recruiting_website.utils.JwtUtil;
import com.queryx.recruiting_website.domain.vo.AdminInfoVo;
import com.queryx.recruiting_website.domain.vo.AdminUserInfoVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.List;
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
    private static final Long SUPER_ADMIN = 1L;

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
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }

        String password = passwordEncoder.encode(adminDto.getAdminPassword());
        adminDto.setAdminPassword(password);
        TDAdmin tdAdmin = new TDAdmin();
        BeanUtils.copyProperties(adminDto, tdAdmin);
        tdAdmin.setAdminStatus("0");
        tdAdmin.setAdminCreateTime(new Date());
        if (!save(tdAdmin)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }

        return null;
    }

    @Override
    public String login(AdminLoginDto adminLoginDto) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(adminLoginDto.getAdminUsername(), adminLoginDto.getAdminPassword());
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        // 判断是否验证通过
        if (Objects.isNull(authenticate)) {
            throw new SystemException(AppHttpCodeEnum.LOGIN_ERROR);
        }
        LoginAdmin loginAdmin = (LoginAdmin) authenticate.getPrincipal();
        HashMap<String, Object> data = new HashMap<>();
        data.put("AdminUser", loginAdmin);

        // 返回前端凭证
        return JwtUtil.createJWT(data);
    }

    @Override
    public AdminUserInfoVo getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginAdmin loginAdmin = (LoginAdmin) authentication.getPrincipal();
        TDAdmin tdAdmin = loginAdmin.getTdAdmin();
        List<String> perms = selectPermsByRoleId(tdAdmin.getRoleId());
        AdminInfoVo adminInfoVo = new AdminInfoVo();
        BeanUtils.copyProperties(tdAdmin, adminInfoVo);
        AdminUserInfoVo adminUserInfoVo = new AdminUserInfoVo();
        adminUserInfoVo.setPermissions(perms);
        adminUserInfoVo.setUser(adminInfoVo);

        return adminUserInfoVo;
    }

    public List<String> selectPermsByRoleId(Long roleId) {
        LambdaQueryWrapper<TPMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(TPMenu::getMenuType, "C", "F");
        wrapper.eq(TPMenu::getStatus, "0");
        // 如果是超级管理员返回所有权限
        if (roleId.equals(SUPER_ADMIN)) {
            List<TPMenu> menus = menuMapper.selectList(wrapper);
            List<String> perms = menus.stream().map(TPMenu::getPerms).collect(Collectors.toList());
            return perms;
        }

        return menuMapper.selectPermsByRoleId(roleId);
    }


}


