package com.queryx.recruiting_website.domain;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


@Data
public class TDAdmin {




    @TableId
    private Long adminId;

    private Long roleId;

    private String adminName;

    private String adminUsername;

    private String adminPassword;

    private String adminStatus;

    private String adminAvatar;

    private Date adminCreateTime;

}

