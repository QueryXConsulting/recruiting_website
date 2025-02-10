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


    @PostMapping("/updateCompanyInfo")
    @Operation(summary = "企业更新")
    public CommonResp updateCompanyInfo(@RequestParam(value = "dtoJson") String jsonDto,
                                        @RequestParam(value = "applyFiles") MultipartFile applyFiles) {

        CompanyInfoDto companyInfoDto = JSON.parseObject(jsonDto, CompanyInfoDto.class);
        return CommonResp.success(tdCompanyInfoService.updateCompanyInfo(companyInfoDto, applyFiles));
    }

//    @DeleteMapping("/deleteCompany/{companyId}")
//    @Operation(summary = "企业删除")
//    public CommonResp deleteCompany(@PathVariable("companyId") Long companyId) {
//        return CommonResp.success(tdCompanyInfoService.deleteCompany(companyId));
//    }

}
