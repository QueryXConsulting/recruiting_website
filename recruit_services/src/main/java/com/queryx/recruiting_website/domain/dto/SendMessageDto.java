package com.queryx.recruiting_website.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SendMessageDto {
    @Schema(name = "userId", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;
    @Schema(name = "content", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
