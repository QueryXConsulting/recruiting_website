package com.queryx.recruiting_website.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class UpdateInterviewDto {


    @Schema(name = "面试ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long interviewId;

    @Schema(name = "简历工作关联id", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long jobResumeId;


    @Schema(name = "用户ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;


    @Schema(name = "工作ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long jobId;

    @Schema(name = "面试结果", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String interviewResult;

    @Schema(name = "面试时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date interviewDate;

    @Schema(name = "面试地点", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String interviewRegion;

    @Schema(name = "面试类型", description = "0代表线上，1代表线下", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String interviewType;

    @Schema(name = "启用状态", description = "0代表拒绝,1代表接受", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String interviewStatus;

    @Schema(name = "面试时长", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer interviewTime;

}
