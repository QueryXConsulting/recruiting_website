package com.queryx.recruiting_website.domain.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class CompanyInfoDto {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyInfoId;

    private String companyInfoUsername;

    private String companyInfoPassword;

    private String companyLogo;

    private String companyInfoName;

    private String companyInfoScope;

    private String companyInfoProfile;

}
