package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class InterviewVO {
    private Long interviewId;
    /**
     * 用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 公司id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyInfoId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long jobId;// 职位id

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
}
