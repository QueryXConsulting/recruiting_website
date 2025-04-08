package com.queryx.recruiting_website.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * (TDCategory)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
@Schema(name = "工种实体类")
public class TDCategory {

    @TableId(type = IdType.AUTO)
    @Schema(name = "分类ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;

    @Schema(name = "工种名称", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoryName;

    @Schema(name = "工种描述", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoryDescription;

    @Schema(name = "启用状态", description = "0代表启用,1代表禁用", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoryStatus;

}

