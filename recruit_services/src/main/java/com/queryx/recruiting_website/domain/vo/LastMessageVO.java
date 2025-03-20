package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LastMessageVO {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "userId", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(name = "用户名", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @Schema(name = "lastMessage", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(name = "createTime", implementation = LocalDateTime.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(name = "isRead", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String isRead;
}
