package com.queryx.recruiting_website.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


import java.util.Date;

/**
 * (TDInterviewDate)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
@Schema(name = "面试时间实体类")
public class TDInterviewDate {

    @TableId(value = "interview_date_id", type = IdType.AUTO)
    @Schema(name = "面试时间id", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long interviewDateId;

    @Schema(name = "公司id", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long companyId;

    @Schema(name = "面试时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date interviewDate;

    @Schema(name = "删除状态", description = "0代表未删除,1代表已删除", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)

    private String isDeleted;

}

