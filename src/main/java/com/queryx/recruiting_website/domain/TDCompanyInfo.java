package com.queryx.recruiting_website.domain;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * (TDCompanyInfo)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
public class TDCompanyInfo {



    /**
     * 企业id
     */
    @TableId
    private Long companyInfoId;
    /**
     * 公司用户id
     */
    private Long userId;
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
     * 企业注册时间
     */
    private Date companyRegisterTime;
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
    /**
     * 0代表待审核,1代表审核通过
     */
    private String companyInfoReview;
    /**
     * 0代表启用,1代表禁用
     */
    private String companyInfoStatus;

}

