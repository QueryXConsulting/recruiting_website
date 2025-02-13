package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.dto.UserCompanyDTO;
import com.queryx.recruiting_website.domain.dto.UserDTO;
import com.queryx.recruiting_website.domain.dto.UserRegisterDTO;
import com.queryx.recruiting_website.domain.vo.UserLoginVO;
import com.queryx.recruiting_website.domain.vo.UserVO;


public interface TDUserService extends IService<TDUser> {


    UserCompanyDTO selectUserInfo(Long userId, String userRole);

    UserCompanyDTO updateUserCompanyInfo(UserCompanyDTO userCompanyDTO);

    UserLoginVO login(LoginDTO loginDTO);

    UserRegisterDTO register(UserRegisterDTO userRegisterDTO);


    Page<UserVO> selectUserList(Integer page, Integer seize, String userName, String userStatus);

    Object updateUserInfo(UserDTO userDTO);

    Object deleteUser(Long userId);

    Object addUser(UserDTO userDTO);
}

