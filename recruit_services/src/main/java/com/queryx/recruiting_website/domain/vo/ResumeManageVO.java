package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;


@Data
public class ResumeManageVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long resumeId;
    private String userName;
    private String resumeType;
    private String resumeReview;
    private String resumeStatus;
}
