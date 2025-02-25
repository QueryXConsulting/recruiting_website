package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDCompanyInfo;
import com.queryx.recruiting_website.domain.dto.RegisterCompanyDto;
import com.queryx.recruiting_website.domain.vo.CompanyInfoDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface TDCompanyInfoService extends IService<TDCompanyInfo> {

    CompanyInfoDto selectCompanyInfo(Long companyId);

    CompanyInfoDto updateCompanyInfo(CompanyInfoDto companyInfoDto, MultipartFile applyFiles, List<MultipartFile> pdfFiles);


    Object selectCompanyInfoList(Integer page, Integer size, String companyStatus, String companyReview, String companyName);

    Object deleteCompany(Long companyId);

    Map<String, byte[]> getEnterpriseFiles(Long companyId) throws IOException;

    Object registerCompany(RegisterCompanyDto registerCompanyDto);
}

