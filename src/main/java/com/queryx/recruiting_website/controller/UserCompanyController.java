package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.dto.JobDetailDto;
import com.queryx.recruiting_website.domain.dto.JobInsertDto;
import com.queryx.recruiting_website.domain.dto.SelectResumeDto;
import com.queryx.recruiting_website.domain.dto.UserCompanyDto;
import com.queryx.recruiting_website.domain.vo.CompanyInfoDto;
import com.queryx.recruiting_website.service.TDCompanyInfoService;
import com.queryx.recruiting_website.service.TDJobService;
import com.queryx.recruiting_website.service.TDResumeService;
import com.queryx.recruiting_website.service.TDUserService;
import com.queryx.recruiting_website.utils.CommonResp;


import com.queryx.recruiting_website.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@Tag(name = "公司用户模块")
@RequestMapping("/company")
@PreAuthorize("hasPermission(null ,'user:company')")
public class UserCompanyController {
    @Resource
    private TDJobService tdJobService;
    @Resource
    private TDUserService tdUserService;
    @Resource
    private TDResumeService tdResumeService;
    @Resource
    private TDCompanyInfoService tdCompanyInfoService;



    @GetMapping("/jobList")
    @Operation(summary = "查看职位列表")
    public CommonResp selectJobList(Integer page, Integer size, Long companyId,String jobName,String jobReview,String status) {
        return CommonResp.success(tdJobService.selectJobList(page, size, companyId,jobName,jobReview,status));
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

    @PostMapping("/publishJob/{companyId}")
    @Operation(summary = "发布职位")
    public CommonResp insertJobInfo(@RequestBody JobInsertDto jobInsertDto, @PathVariable("companyId") Long companyId) {
        return CommonResp.success(tdJobService.insertJobInfo(jobInsertDto, companyId));
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

    @PutMapping("/updateCompanyInfo")
    @Operation(summary = "更新公司信息")
    public CommonResp updateCompanyInfo(@RequestBody CompanyInfoDto companyInfoDto) {
        return CommonResp.success(tdCompanyInfoService.updateCompanyInfo(companyInfoDto));
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
