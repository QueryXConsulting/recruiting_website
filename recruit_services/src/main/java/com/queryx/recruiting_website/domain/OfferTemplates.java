package com.queryx.recruiting_website.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;


@Data
@Schema(description = "offers模板类")
public class OfferTemplates {

    @TableId(type = IdType.AUTO)
    @Schema(name = "offerTemplatesId", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long offerTemplatesId;

    @Schema(name = "模板名", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String templateName;

    @Schema(name = "创建时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date createdAt;

    @Schema(name = "更新时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date updatedAt;

    @Schema(name = "offer模板文件存储路径", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String templateFilePath;


}

