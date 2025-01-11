package com.queryx.recruiting_website.domain.vo;

import lombok.Data;

@Data
public class ResumeListVo {
    private Long resumeId;
    private String resumeName;
    private String resumeType;
    private String jobPosition;
}
