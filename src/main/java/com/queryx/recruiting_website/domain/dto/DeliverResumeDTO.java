package com.queryx.recruiting_website.domain.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DeliverResumeDTO {

    /**
     * 职位ID
     */
    private Long jobId;

    /**
     * 简历ID
     */
    private Long resumeId;

    /**
     * 简历类型
     */
    private String resumeType;

    /**
     * 简历名称
     */
    private String resumeName;
}
