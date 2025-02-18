package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class NatureVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long natureId;
    private String jobNatureName;
    private String natureStatus;
}
