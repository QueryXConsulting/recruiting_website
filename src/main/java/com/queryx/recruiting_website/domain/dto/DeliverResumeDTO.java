package com.queryx.recruiting_website.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(name = "投递简历DTO")
public class DeliverResumeDTO {

    @Schema(name = "职位ID", requiredMode = Schema.RequiredMode.REQUIRED, implementation = Long.class)
    private Long jobId;

    @Schema(name = "简历ID", requiredMode = Schema.RequiredMode.REQUIRED, implementation = Long.class)
    private Long resumeId;

    @Schema(name = "简历类型", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeType;

    @Schema(name = "简历名称", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeName;
}
