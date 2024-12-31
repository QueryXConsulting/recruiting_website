package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDAdmin;
import com.queryx.recruiting_website.domain.dto.AdminDto;
import com.queryx.recruiting_website.domain.dto.AdminLoginDto;
import com.queryx.recruiting_website.domain.vo.AdminUserInfoVo;


public interface TDAdminService extends IService<TDAdmin> {

    AdminDto addAdmin(AdminDto adminDto);

    String login(AdminLoginDto adminLoginDto);


    AdminUserInfoVo getInfo();
}

