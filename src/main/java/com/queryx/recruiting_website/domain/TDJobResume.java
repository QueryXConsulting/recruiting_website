package com.queryx.recruiting_website.domain;

import java.io.Serializable;
import lombok.Data;

/**
 * (TDJobResume)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
public class TDJobResume {

    /**
     * 表id
     */
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
     * 简历类型(0代表线上简历,1代表附件简历)
     */
    private String resumeType;

}

