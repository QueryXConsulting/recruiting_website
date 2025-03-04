package com.queryx.recruiting_website.domain.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class InterviewListVo {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "面试ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long interviewId;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "投递简历ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long jobResumeId;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "用户ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "工作ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long jobId;

    @Schema(name = "面试时长", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer interviewTime;

    @Schema(name = "面试工作名称", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobName;

    @Schema(name = "面试结果", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String interviewResult;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "面试时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date interviewDate;

    @Schema(name = "面试地点", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String interviewRegion;

    @Schema(name = "面试类型", description = "0代表线上，1代表线下", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String interviewType;

    @Schema(name = "启用状态", description = "0代表拒绝,1代表接受", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String interviewStatus;

    @Schema(name = "简历名称", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String ResumeName;
}
