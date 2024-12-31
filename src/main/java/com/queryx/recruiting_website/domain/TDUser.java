package com.queryx.recruiting_website.domain;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


@Data
public class TDUser {

    @TableId
    private Long userId;

    private Long resumeId;

    private String userName;

    private String userPhone;

    private String userPassword;

    private String userRole;

    private String userStatus;

    private String userAvatar;

    private Integer userInterviews;

    private Date userRegisterTime;

}

