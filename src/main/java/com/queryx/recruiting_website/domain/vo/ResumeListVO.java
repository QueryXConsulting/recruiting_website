package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ResumeListVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long resumeId;
    private String resumeName;
    private String resumeType;
    private String jobPosition;
}
