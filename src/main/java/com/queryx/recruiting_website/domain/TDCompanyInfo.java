package com.queryx.recruiting_website.domain;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


@Data
public class TDCompanyInfo {




    @TableId
    private Long companyInfoId;

    private Long userId;

    private String companyInfoUsername;

    private String companyInfoPassword;

    private String companyLogo;

    private Date companyRegisterTime;

    private String companyInfoName;

    private String companyInfoScope;

    private String companyInfoProfile;

    private String companyInfoReview;

    private String companyInfoStatus;

}

