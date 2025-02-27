package com.queryx.recruiting_website.domain.dto;

import lombok.Data;

@Data
public class SendInterviewDto {
    private Long resumeId;
    private Long userId;
    private String resumeType;
    private Long jobId;
    private String interviewRegion;
    private String interviewType;
    private int interviewTime;
}
