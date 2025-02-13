package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDAdmin;
import com.queryx.recruiting_website.domain.dto.AdminDTO;
import com.queryx.recruiting_website.domain.dto.AdminLoginDTO;
import com.queryx.recruiting_website.domain.vo.AdminUserInfoVO;
import com.queryx.recruiting_website.domain.vo.AdminVO;


public interface TDAdminService extends IService<TDAdmin> {

    AdminDTO addAdmin(AdminDTO adminDTO);

    String login(AdminLoginDTO adminLoginDTO);


    AdminUserInfoVO getInfo();

    Page<AdminVO> selectAdminList(Integer page, Integer size, String adminName, String adminStatus);

    AdminVO selectAdminInfo(Long userId);

    Object updateAdminInfo(AdminDTO adminDTO);

    Object deleteAdmin(Long adminId);
}

