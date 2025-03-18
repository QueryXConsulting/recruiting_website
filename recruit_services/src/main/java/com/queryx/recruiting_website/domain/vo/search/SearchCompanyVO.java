package com.queryx.recruiting_website.domain.vo.search;

import lombok.Data;

/**
 * @Author：fjj
 * @Date：2025/3/18 15:03
 */
@Data
public class SearchCompanyVO extends SearchResultVO{

    // 公司LOGO
    private String companyLogo;
    // 公司名称
    private String companyInfoName;
    // 公司经营范围
    private String companyInfoScope;




}
