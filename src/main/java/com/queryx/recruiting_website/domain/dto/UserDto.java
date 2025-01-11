package com.queryx.recruiting_website.domain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDto {

    private Long userId;

    private Long resumeId;

    private String userName;

    private String userPhone;

    private String userPassword;

    private String userRole;

    private String userStatus;

    private String userAvatar;

}
