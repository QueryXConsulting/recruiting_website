package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDCompanyInfo;
import com.queryx.recruiting_website.domain.vo.CompanyInfoDto;



public interface TDCompanyInfoService extends IService<TDCompanyInfo> {

    CompanyInfoDto selectCompanyInfo(Long companyId);

    CompanyInfoDto updateCompanyInfo(CompanyInfoDto companyInfoDto);
}

