package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class AdminVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long adminId;

    private String roleName;

    private String adminName;

    private String adminUsername;

    private String adminStatus;
}
