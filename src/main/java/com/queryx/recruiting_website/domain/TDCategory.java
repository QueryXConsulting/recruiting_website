package com.queryx.recruiting_website.domain;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * (TDCategory)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
public class TDCategory {



    /**
     * 分类id
     */
    @TableId
    private Long categoryId;
    /**
     * 工种名称
     */
    private String categoryName;
    /**
     * 0代表启用,1代表禁用
     */
    private String categoryStatus;

}

