package com.queryx.recruiting_website.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * (TDJobNature)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
public class TDJobNature {



    /**
     * 工作性质id
     */
    @TableId(type = IdType.AUTO)
    private Long natureId;
    /**
     * 工作性质名称
     */
    private String jobNatureName;
    /**
     * 0代表启用,1代表禁用
     */
    private String natureStatus;

}

