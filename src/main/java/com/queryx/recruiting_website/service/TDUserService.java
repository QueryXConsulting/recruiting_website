package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.dto.UserCompanyDto;
import com.queryx.recruiting_website.domain.dto.UserRegisterDTO;
import com.queryx.recruiting_website.domain.vo.UserLoginVo;


public interface TDUserService extends IService<TDUser> {


    UserCompanyDto selectUserInfo(Long userId, String userRole);

    UserCompanyDto updateUserInfo(UserCompanyDto userCompanyDto);

    UserLoginVo login(LoginDTO loginDTO);

    UserRegisterDTO register(UserRegisterDTO userRegisterDTO);
}

