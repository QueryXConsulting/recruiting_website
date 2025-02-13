package com.queryx.recruiting_website.domain.dto;

import lombok.Data;

@Data
public class UserDTO {

    private Long userId;

    private Long resumeId;

    private String userName;

    private String userPhone;

    private String userPassword;

    private String userEmail;

    private String userRole;

    private String userStatus;

    private String userAvatar;

}
