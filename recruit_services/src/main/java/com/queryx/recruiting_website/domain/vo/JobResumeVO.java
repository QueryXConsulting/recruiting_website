package com.queryx.recruiting_website.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author fjj
 * @date 2025/2/26
 */
@Data
public class JobResumeVO {

    @Schema(name = "职位ID", requiredMode = Schema.RequiredMode.REQUIRED, implementation = Long.class)
    private Long jobId;

    @Schema(name = "简历ID", requiredMode = Schema.RequiredMode.REQUIRED, implementation = Long.class)
    private Long resumeId;

    @Schema(name = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, implementation = Long.class)
    private Long userId;

    @Schema(name = "职位名称", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String jobPosition;

    @Schema(name = "公司名", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String companyInfoName;

    @Schema(name = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeName;

    @Schema(name = "简历类型", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private String resumeType;

    @Schema(name = "简历投递状态", description = "1(已查看) 2(待面试) 0:用户端(已投递), 公司端(待查看)", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumeStatus;

    @Schema(name = "简历通过状态", description = "为0时则代表不合适", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumeDelete;

    @Schema(name = "简历投递时间", requiredMode = Schema.RequiredMode.REQUIRED, implementation = Date.class)
    private Date deliverDate;

}
