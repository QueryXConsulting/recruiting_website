package com.queryx.recruiting_website.controller;


import com.alibaba.fastjson2.JSON;
import com.queryx.recruiting_website.domain.vo.CompanyInfoDto;
import com.queryx.recruiting_website.service.TDCompanyInfoService;
import com.queryx.recruiting_website.utils.CommonResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Tag(name = "管理员下企业管理模块")
public class CompanyManageController {

    @Resource
    private TDCompanyInfoService tdCompanyInfoService;

    @GetMapping("/companyList")
    @Operation(summary = "企业列表")
    public CommonResp companyList(Integer page, Integer size, String companyStatus, String companyReview, String companyName) {
        return CommonResp.success(tdCompanyInfoService.selectCompanyInfoList(page, size, companyStatus, companyReview, companyName));
    }

    @GetMapping ("/enterpriseFiles/{companyInfoId}")
    @Operation(summary = "企业资质文件查看")
    public CommonResp getEnterpriseFiles(@PathVariable(value = "companyInfoId") Long companyInfoId) throws IOException {
        Map<String, byte[]> files = tdCompanyInfoService.getEnterpriseFiles(companyInfoId);
        return CommonResp.success(files);
    }

    @PostMapping("/updateCompanyInfo")
    @Operation(summary = "企业更新")
    public CommonResp updateCompanyInfo(@RequestParam(value = "dtoJson") String jsonDto,
                                        @RequestParam(value = "applyFiles", required = false) MultipartFile applyFiles,
                                        @RequestParam(value = "pdfFiles", required = false) List<MultipartFile> pdfFiles
    ) {
        CompanyInfoDto companyInfoDto = JSON.parseObject(jsonDto, CompanyInfoDto.class);
        return CommonResp.success(tdCompanyInfoService.updateCompanyInfo(companyInfoDto, applyFiles, pdfFiles));
    }

//    @DeleteMapping("/deleteCompany/{companyId}")
//    @Operation(summary = "企业删除")
//    public CommonResp deleteCompany(@PathVariable("companyId") Long companyId) {
//        return CommonResp.success(tdCompanyInfoService.deleteCompany(companyId));
//    }

}
