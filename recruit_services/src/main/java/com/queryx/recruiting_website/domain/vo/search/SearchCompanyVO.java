package com.queryx.recruiting_website.domain.vo.search;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author：fjj
 * @Date：2025/3/18 15:03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchCompanyVO extends SearchResultVO{

    // 公司ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyInfoId;
    // 公司LOGO
    private String companyLogo;
    // 公司名称
    private String companyInfoName;
    // 公司介绍
    private String companyInfoProfile;




}
