package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDAdmin;
import com.queryx.recruiting_website.domain.dto.AdminDto;
import com.queryx.recruiting_website.domain.dto.AdminLoginDto;
import com.queryx.recruiting_website.domain.vo.AdminUserInfoVo;
import com.queryx.recruiting_website.domain.vo.AdminVo;


public interface TDAdminService extends IService<TDAdmin> {

    AdminDto addAdmin(AdminDto adminDto);

    String login(AdminLoginDto adminLoginDto);


    AdminUserInfoVo getInfo();

    Page<AdminVo> selectAdminList(Integer page, Integer size, String adminName, String adminStatus);

    AdminVo selectAdminInfo(Long userId);

    Object updateAdminInfo(AdminDto adminDto);

    Object deleteAdmin(Long adminId);
}

