package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDCompanyInfo;
import com.queryx.recruiting_website.vo.CompanyInfoVo;


/**
 * (TDCompanyInfo)表服务接口
 *
 * @author makejava
 * @since 2024-12-23 13:10:59
 */
public interface TDCompanyInfoService extends IService<TDCompanyInfo> {

    CompanyInfoVo selectCompanyInfo(Long companyId);

    CompanyInfoVo updateCompanyInfo(CompanyInfoVo companyInfoVo);
}

