package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;


@Data
@Schema(description = "offers类")
public class OffersVO {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "offerID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long offersId;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "用户ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "工作ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long jobId;

    @Schema(name = "应聘者名称", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @Schema(name = "offers状态", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String offersStatus;

    @Schema(name = "发送时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date offersDate;

    @Schema(name = "offer文件存储路径", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String offersFilePath;

}

