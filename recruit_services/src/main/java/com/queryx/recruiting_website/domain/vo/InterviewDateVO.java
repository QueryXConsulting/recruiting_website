package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 面试时间
 *
 * @author fjj
 * @since 2025-2-27
 */
@Data
@Schema(name = "面试时间VO类")
public class InterviewDateVO {

    @Schema(name = "面试时间id", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long interviewDateId;

    @Schema(name = "公司id", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long companyId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "面试时间段(开始时间)", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date interviewDateStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "面试时间段(结束时间)", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date interviewDateEnd;

    @Schema(name = "删除状态", description = "0代表未删除,1代表已删除", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String isDeleted;

}

