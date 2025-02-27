package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ResumeListVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long resumeId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private String resumeName;
    private String resumeType;
    private String resumeStatus;
    private String resumeDelete;
    private String interviewStatus;
}
