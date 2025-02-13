package com.queryx.recruiting_website.controller;

import com.alibaba.fastjson2.JSON;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.dto.JobDetailDto;
import com.queryx.recruiting_website.domain.dto.JobInsertDto;
import com.queryx.recruiting_website.domain.dto.SelectResumeDto;
import com.queryx.recruiting_website.domain.dto.UserCompanyDto;
import com.queryx.recruiting_website.domain.vo.CompanyInfoDto;
import com.queryx.recruiting_website.service.*;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@Tag(name = "公司用户模块")
@RequestMapping("/company")
public class UserCompanyController {
    @Resource
    private TDJobService tdJobService;
    @Resource
    private TDUserService tdUserService;
    @Resource
    private TDResumeService tdResumeService;
    @Resource
    private TDCompanyInfoService tdCompanyInfoService;
    @Resource
    private TDCategoryService categoryService;


    @GetMapping("/selectCategory")
    @Operation(summary = "工种分类列表查询")
    public CommonResp selectCategoryList(String categoryName) {
        return CommonResp.success(categoryService.selectCategoryList(1,100,categoryName,"0"));
    }

    @GetMapping("/jobList")
    @Operation(summary = "公司工作列表查询")
    public CommonResp selectJobList(Integer page, Integer size,String jobName
            ,String jobReview,String jobCategory) {
        return CommonResp.success(tdJobService.selectCompanyJobList(page, size,jobName,jobReview,jobCategory));
    }

    @GetMapping("/userCompanyList")
    @Operation(summary = "公司员工列表查询")
    public CommonResp selectUserCompanyList(Integer page, Integer size,String userName) {
        return CommonResp.success(tdUserService.selectUserCompanyList(page, size,userName));
    }

    @GetMapping("/jobInfo/{jobId}")
    @Operation(summary = "查看职位信息")
    public CommonResp selectJobInfo(@PathVariable("jobId") Long jobId) {
        return CommonResp.success(tdJobService.selectJobInfo(jobId));
    }

    @PutMapping("/updateJob")
    @Operation(summary = "更新职位信息")
    public CommonResp updateJob(@RequestBody JobDetailDto jobDetailDto) {
        return CommonResp.success(tdJobService.updateJob(jobDetailDto));
    }

    @PostMapping("/publishJob")
    @Operation(summary = "发布职位")
    public CommonResp insertJobInfo(@RequestBody JobInsertDto jobInsertDto) {
        return CommonResp.success(tdJobService.insertJobInfo(jobInsertDto));
    }

    @DeleteMapping("/deleteJob/{jobId}")
    @Operation(summary = "删除职位")
    public CommonResp deleteJob(@PathVariable("jobId") Long jobId) {
        return CommonResp.success(tdJobService.deleteJob(jobId));
    }

    @GetMapping("/selectUserInfo")
    @Operation(summary = "查询个人信息")
    public CommonResp selectUserInfo() {
        TDUser tdUser = SecurityUtils.getLoginUser().getTdUser();
        return CommonResp.success(tdUserService.selectUserInfo(tdUser.getUserId(), tdUser.getUserRole()));
    }

    @PutMapping("/updateUserInfo")
    @Operation(summary = "更新个人信息")
    public CommonResp updateUserInfo(@RequestBody UserCompanyDto userCompanyDto) {
        return CommonResp.success(tdUserService.updateUserCompanyInfo(userCompanyDto));
    }


    @GetMapping("/selectCompanyInfo/{companyId}")
    @Operation(summary = "查询公司信息")
    public CommonResp selectCompanyInfo(@PathVariable("companyId") Long companyId) {
        return CommonResp.success(tdCompanyInfoService.selectCompanyInfo(companyId));
    }

    @PostMapping("/updateCompanyInfo")
    @Operation(summary = "更新公司信息")
    public CommonResp updateCompanyInfo(
            @RequestParam(value = "dtoJson") String jsonDto,
            @RequestParam(value = "applyFiles", required = false) MultipartFile applyFiles,
            @RequestParam(value = "pdfFiles", required = false) List<MultipartFile> pdfFiles) {
        CompanyInfoDto companyInfoDto = JSON.parseObject(jsonDto,CompanyInfoDto.class);
        return CommonResp.success(tdCompanyInfoService.updateCompanyInfo(companyInfoDto,applyFiles, pdfFiles));
    }


    @GetMapping("/resumeList/{companyId}")
    @Operation(summary = "查询投递的简历列表")
    public CommonResp selectResumeList(@PathVariable("companyId") Long companyId,Integer page, Integer size) {
        return CommonResp.success(tdResumeService.selectResumeList(page,size,companyId));
    }

    @PostMapping("/selectResume")
    @Operation(summary = "查看简历")
    public CommonResp selectResume(@RequestBody SelectResumeDto selectResumeDto) {
        return CommonResp.success(tdResumeService.selectResume(selectResumeDto));
    }
}
