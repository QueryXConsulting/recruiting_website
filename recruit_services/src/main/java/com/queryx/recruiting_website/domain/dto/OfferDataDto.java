package com.queryx.recruiting_website.domain.dto;

import lombok.Data;

@Data
public class OfferDataDto {
    private Long offerId;
    private String templatePath;
    private String companyName;
    private String userName;
    private String workTime;
    private String position;
    private String salary;
    private String welfare;
    private String workLocation;
    private String material;
}
