package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class CategoryVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
    private String categoryStatus;
}
