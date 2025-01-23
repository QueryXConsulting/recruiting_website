package com.queryx.recruiting_website.controller;


import com.queryx.recruiting_website.service.TDCompanyInfoService;
import com.queryx.recruiting_website.utils.CommonResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Tag(name = "管理员下企业管理模块")
public class CompanyManageController {

    @Resource
    private TDCompanyInfoService companyInfoService;

    @GetMapping("/companyList")
    @Operation(summary = "公司列表")
    public CommonResp companyList(Integer page,Integer size,String companyReview, String companyStatus, String companyName) {
        return CommonResp.success(companyInfoService.companyList(page,size,companyReview, companyStatus, companyName));
    }
}
