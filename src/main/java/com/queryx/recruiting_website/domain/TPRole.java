package com.queryx.recruiting_website.domain;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


@Data
public class TPRole {




    @TableId
    private Long roleId;

    private String roleName;

    private Integer roleSort;

    private String status;

    private String delFlag;

    private Date createTime;

    private Long updateBy;

    private Date updateTime;

}

