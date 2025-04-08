package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDataVO {
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(name = "messageId", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long messageId;

    @Schema(name = "content", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(name = "留言所属者名称", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String user;

    @Schema(name = "留言所属者标识", description = "留言所属者标识0为用户1为公司", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String ownerUser;

    @Schema(name = "userId", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(name = "createTime", implementation = LocalDateTime.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(name = "isRead", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String isRead;
}
