package com.queryx.recruiting_website.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class UpdateInterviewDto {


    @Schema(name = "面试ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long interviewId;

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
