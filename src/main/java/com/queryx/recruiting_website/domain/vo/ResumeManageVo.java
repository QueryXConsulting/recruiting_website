package com.queryx.recruiting_website.domain.vo;

import lombok.Data;


@Data
public class ResumeManageVo {
    private Long resumeId;
    private String userName;
    private String resumeType;
    private String resumeReview;
    private String resumeStatus;
}
