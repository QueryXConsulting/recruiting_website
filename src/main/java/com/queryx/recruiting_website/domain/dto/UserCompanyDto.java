package com.queryx.recruiting_website.domain.dto;

import lombok.Data;

@Data
public class UserCompanyDto {

    private Long userId;


    private String userName;

    private String userPhone;

    private String userPassword;

    private String userRole;

    private String userAvatar;
}
