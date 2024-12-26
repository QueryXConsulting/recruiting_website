package com.queryx.recruiting_website.vo;


import lombok.Data;

import java.util.Date;

@Data
public class CompanyInfoVo {
    /**
     * 企业id
     */
    private Long companyInfoId;
    /**
     * 账号
     */
    private String companyInfoUsername;
    /**
     * 密码
     */
    private String companyInfoPassword;
    /**
     * 公司logo
     */
    private String companyLogo;
    /**
     * 企业名
     */
    private String companyInfoName;
    /**
     * 公司经营范围
     */
    private String companyInfoScope;
    /**
     * 公司介绍
     */
    private String companyInfoProfile;

}
