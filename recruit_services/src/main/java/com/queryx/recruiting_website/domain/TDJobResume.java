package com.queryx.recruiting_website.domain;



import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;


/**
 * (TDJobResume)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
@TableName("t_d_job_resume")
@Schema(name = "投递简历实体类")
public class TDJobResume {

    @TableId(value = "job_resume_id", type = IdType.AUTO)
    @Schema(name = "表ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long jobResumeId;

    @Schema(name = "工作ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long jobId;

    @Schema(name = "简历ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long resumeId;

    @Schema(name = "简历类型", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumeType;

    @Schema(name = "简历名称", description = "默认是用户名", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumeName;

    @Schema(name = "简历投递状态", description = "1(已查看) 2(待面试) 0:用户端(已投递), 公司端(待查看)", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String resumeStatus;

    @Schema(name = "用户id", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(name = "简历投递时间", requiredMode = Schema.RequiredMode.REQUIRED, implementation = Date.class)
    private Date deliverDate;

}

