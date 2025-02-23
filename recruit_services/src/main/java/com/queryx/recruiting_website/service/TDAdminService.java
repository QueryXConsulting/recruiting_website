package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDAdmin;
import com.queryx.recruiting_website.domain.dto.AdminDto;
import com.queryx.recruiting_website.domain.dto.AdminLoginDto;
import com.queryx.recruiting_website.domain.vo.AdminUserInfoVO;
import com.queryx.recruiting_website.domain.vo.AdminVO;
import com.queryx.recruiting_website.domain.vo.UserLoginVO;


public interface TDAdminService extends IService<TDAdmin> {

    AdminDto addAdmin(AdminDto adminDto);

    UserLoginVO login(AdminLoginDto adminLoginDto);


    AdminUserInfoVO getInfo();

    Page<AdminVO> selectAdminList(Integer page, Integer size, String adminName, String adminStatus);

    AdminVO selectAdminInfo(Long userId);

    Object updateAdminInfo(AdminDto adminDto);

    Object deleteAdmin(Long adminId);
}

