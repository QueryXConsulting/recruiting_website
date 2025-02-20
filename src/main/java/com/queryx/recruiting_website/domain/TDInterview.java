package com.queryx.recruiting_website.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * (TDInterview)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
@ToString
@Schema(name = "面试信息实体类")
public class TDInterview {

    @TableId(value = "interview_id", type = IdType.AUTO)
    @Schema(name = "面试ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long interviewId;

    @Schema(name = "用户ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(name = "公司ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long companyId;

    @Schema(name = "面试工作名", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String interviewJob;

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

    @Schema(name = "删除状态", description = "0代表未删除,1代表已删除", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)

    private String isDeleted;

}

