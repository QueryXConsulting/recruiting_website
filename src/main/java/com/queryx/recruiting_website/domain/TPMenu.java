package com.queryx.recruiting_website.domain;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.queryx.recruiting_website.domain.vo.MenuVo;
import lombok.Data;


@Data
public class TPMenu {




    @TableId
    private Long menuId;

    private String menuName;

    private Long parentId;

    private Integer orderNum;

    private String path;

    private String component;

    private String menuType;

    private String visible;

    private String status;

    private String perms;

    private String icon;

    private Long createBy;

    private Date createTime;

    private Long updateBy;

    private Date updateTime;

    private String remark;

    private String delFlag;

    @TableField(exist = false)
    private List<MenuVo> children;

}

