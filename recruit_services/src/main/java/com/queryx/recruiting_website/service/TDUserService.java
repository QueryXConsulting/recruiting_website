package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.dto.LoginDTO;
import com.queryx.recruiting_website.domain.dto.UserCompanyDto;
import com.queryx.recruiting_website.domain.dto.UserDto;
import com.queryx.recruiting_website.domain.dto.UserRegisterDTO;
import com.queryx.recruiting_website.domain.vo.UserLoginVO;
import com.queryx.recruiting_website.domain.vo.UserVO;
import com.queryx.recruiting_website.utils.CommonResp;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface TDUserService extends IService<TDUser> {


    UserCompanyDto selectUserInfo(Long userId, String userRole);

    UserCompanyDto updateUserCompanyInfo(UserCompanyDto userCompanyDto);

    UserLoginVO login(LoginDTO loginDTO);

    UserRegisterDTO register(UserRegisterDTO userRegisterDTO);


    Page<UserVO> selectUserList(Integer page, Integer seize, String userName, String userStatus);

    Object updateUserInfo(UserDto userDto);

    Object deleteUser(Long userId);

    Object addUser(UserDto userDto);

    Object selectUserCompanyList(Integer page, Integer size, String userName);

    Object updateUserCompany(UserDto userDto, MultipartFile userAvatar) throws IOException;

    Object addUserCompany(UserDto userDto, MultipartFile userAvatar) throws IOException;

    /**
     * 管理端修改用户头像
     *
     * @param userId 用户id
     * @param image  用户头像
     * @return 头像相对路径
     */
    CommonResp<String> userUploadAvatar(Long userId, MultipartFile image);


    /**
     * 管理端修改管理员头像
     *
     * @param adminId 管理员id
     * @param image  管理员头像
     * @return 头像相对路径
     */
    CommonResp<String> adminUploadAvatar(Long adminId, MultipartFile image);
}

