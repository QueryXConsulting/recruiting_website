package com.queryx.recruiting_website.domain.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class InterviewDateVO {
    @JsonSerialize(using = StringSerializer.class)
    @Schema(name = "面试时间id", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long interviewDateId;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "面试时间开始范围", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date interviewDateStart;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "面试时间结束范围", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date interviewDateEnd;
}
