package com.queryx.recruiting_website.domain;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * (TDInterview)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
public class TDInterview {



    /**
     * 面试表id
     */
    private Long interviewId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 面试工作名称
     */
    private String interviewJob;
    /**
     * 面试结果
     */
    private String interviewResult;
    /**
     * 面试时间
     */
    private Date interviewDate;
    /**
     * 面试地点
     */
    private String interviewRegion;
    /**
     * 0代表线上，1代表线下
     */
    private String interviewType;
    /**
     * 0代表拒绝,1代表接受
     */
    private String interviewStatus;
    /**
     * 0代表未删除,1代表已删除
     */
    private String isDeleted;

}

