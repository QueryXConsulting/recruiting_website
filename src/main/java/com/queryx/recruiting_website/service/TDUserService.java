package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.dto.UserCompanyDto;
import com.queryx.recruiting_website.domain.dto.UserDto;
import com.queryx.recruiting_website.domain.dto.UserRegisterDTO;
import com.queryx.recruiting_website.domain.vo.UserLoginVo;
import com.queryx.recruiting_website.domain.vo.UserVo;


public interface TDUserService extends IService<TDUser> {


    UserCompanyDto selectUserInfo(Long userId, String userRole);

    UserCompanyDto updateUserCompanyInfo(UserCompanyDto userCompanyDto);

    UserLoginVo login(LoginDTO loginDTO);

    UserRegisterDTO register(UserRegisterDTO userRegisterDTO);


    Page<UserVo> selectUserList(Integer page, Integer seize, String userName, String userStatus);

    Object updateUserInfo(UserDto userDto);

    Object deleteUser(Long userId);

    Object addUser(UserDto userDto);
}

