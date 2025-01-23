package com.queryx.recruiting_website.domain.dto;

import lombok.Data;

@Data
public class JobNatureDto {
    private Long natureId;
    private String jobNatureName;
    private String natureStatus;
}
