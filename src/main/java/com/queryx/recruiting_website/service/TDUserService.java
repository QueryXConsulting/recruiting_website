package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.dto.UserCompanyDto;
import com.queryx.recruiting_website.domain.dto.UserRegisterDTO;


public interface TDUserService extends IService<TDUser> {


    UserCompanyDto selectUserInfo(Long userId, String userRole);

    UserCompanyDto updateUserInfo(UserCompanyDto userCompanyDto);

    String login(LoginDTO loginDTO);

    UserRegisterDTO register(UserRegisterDTO userRegisterDTO);
}

