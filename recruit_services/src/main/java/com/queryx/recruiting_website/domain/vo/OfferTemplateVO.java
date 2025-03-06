package com.queryx.recruiting_website.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;


@Data
@Schema(description = "offers模板类VO")
public class OfferTemplateVO {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "offerTemplatesId", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long offerTemplatesId;

    @Schema(name = "模板名", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String templateName;

    @Schema(name = "offer模板文件存储路径", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String templateFilePath;

    @Schema(name = "offer模板缩略图路径", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String templateImg;


}

