package com.queryx.recruiting_website.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

/**
 * (TDJobResume)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
@ToString
@TableName("t_d_job_resume")
public class TDJobResume {

    /**
     * 表id
     */
    @TableId(value = "job_resume_id", type = IdType.AUTO)
    private Long jobResumeId;
    /**
     * 工作id
     */
    private Long jobId;
    /**
     * 简历id
     */
    private Long resumeId;
    /**
     * 简历类型
     */
    private String resumeType;
    /**
     * 简历名（默认用户名）
     */
    private String resumeName;

}

