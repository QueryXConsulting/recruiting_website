package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDCompanyInfo;
import com.queryx.recruiting_website.domain.dto.UserRegisterDTO;


public interface TDCompanyInfoService extends IService<TDCompanyInfo> {

    UserRegisterDTO.CompanyInfoDTO selectCompanyInfo(Long companyId);

    UserRegisterDTO.CompanyInfoDTO updateCompanyInfo(UserRegisterDTO.CompanyInfoDTO companyInfoDTO);
}

