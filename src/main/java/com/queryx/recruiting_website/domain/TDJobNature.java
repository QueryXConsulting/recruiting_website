package com.queryx.recruiting_website.domain;

import java.io.Serializable;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * (TDJobNature)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
@Schema(name = "工作性质实体类")
public class TDJobNature {



    @TableId(type = IdType.AUTO)
    @Schema(name = "工作性质ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long natureId;

    @Schema(name = "工作性质名称", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobNatureName;

    @Schema(name = "启用状态", description = "0代表启用,1代表禁用", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)

    private String natureStatus;

}

