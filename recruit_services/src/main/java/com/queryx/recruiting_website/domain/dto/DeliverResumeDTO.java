package com.queryx.recruiting_website.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(name = "投递简历DTO")
public class DeliverResumeDTO {

    @Schema(name = "投递ID", implementation = Long.class)
    private Long jobResumeId;

    @Schema(name = "职位ID", requiredMode = Schema.RequiredMode.REQUIRED, implementation = Long.class)
    private Long jobId;

    @Schema(name = "简历ID", requiredMode = Schema.RequiredMode.REQUIRED, implementation = Long.class)
    private Long resumeId;

    @Schema(name = "简历类型", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeType;

    @Schema(name = "简历名称", description = "默认为用户名称", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeName;

    @Schema(name = "用户id", requiredMode = Schema.RequiredMode.REQUIRED, implementation = Long.class)
    private Long userId;

    @Schema(name = "简历投递状态", description = "-1(撤销投递) 1(已查看) 2(待面试) 0:用户端(已投递), 公司端(待查看)", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumeStatus;
}
