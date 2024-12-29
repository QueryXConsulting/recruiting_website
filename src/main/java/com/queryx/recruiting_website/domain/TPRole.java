package com.queryx.recruiting_website.domain;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * (TPRole)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
public class TPRole {



    /**
     * 角色ID
     */
    @TableId
    private Long roleId;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 显示顺序
     */
    private Integer roleSort;
    /**
     * 角色状态(0正常 1停用)
     */
    private String status;
    /**
     * 0代表未删除,1代表已删除
     */
    private String delFlag;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新者
     */
    private Long updateBy;
    /**
     * 更新时间
     */
    private Date updateTime;

}

