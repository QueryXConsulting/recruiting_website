package com.queryx.recruiting_website.controller;

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


import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


@RestController
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



    @GetMapping("/jobList")
    public CommonResp selectJobList(Integer page, Integer size, Long companyId) {
        return CommonResp.success(tdJobService.selectJobList(page, size, companyId));
    }

    @GetMapping("/jobInfo/{jobId}")
    public CommonResp selectJobInfo(@PathVariable("jobId") Long jobId) {
        return CommonResp.success(tdJobService.selectJobInfo(jobId));
    }

    @PostMapping("/updateJob")
    public CommonResp updateJob(@RequestBody JobDetailDto jobDetailDto) {
        return CommonResp.success(tdJobService.updateJob(jobDetailDto));
    }

    @PostMapping("/publishJob/{companyId}")
    public CommonResp insertJobInfo(@RequestBody JobInsertDto jobInsertDto, @PathVariable("companyId") Long companyId) {
        return CommonResp.success(tdJobService.insertJobInfo(jobInsertDto, companyId));
    }

    @DeleteMapping("/deleteJob/{jobId}")
    public CommonResp deleteJob(@PathVariable("jobId") Long jobId) {
        return CommonResp.success(tdJobService.deleteJob(jobId));
    }

    @GetMapping("/selectUserInfo")
    public CommonResp selectUserInfo(Long userId, String userRole) {
        return CommonResp.success(tdUserService.selectUserInfo(userId, userRole));
    }

    @PostMapping("/updateUserInfo")
    public CommonResp updateUserInfo(@RequestBody UserCompanyDto userCompanyDto) {
        return CommonResp.success(tdUserService.updateUserInfo(userCompanyDto));
    }


    @GetMapping("/selectCompanyInfo/{companyId}")
    public CommonResp selectCompanyInfo(@PathVariable("companyId") Long companyId) {
        return CommonResp.success(tdCompanyInfoService.selectCompanyInfo(companyId));
    }

    @PostMapping("/updateCompanyInfo")
    public CommonResp updateCompanyInfo(@RequestBody CompanyInfoDto companyInfoDto) {
        return CommonResp.success(tdCompanyInfoService.updateCompanyInfo(companyInfoDto));
    }

    @GetMapping("/resumeList/{companyId}")
    public CommonResp selectResumeList(@PathVariable("companyId") Long companyId) {
        return CommonResp.success(tdResumeService.insertJobInfo(companyId));
    }

    @PostMapping("/selectResume")
    public CommonResp selectResume(@RequestBody SelectResumeDto selectResumeDto) {
        return CommonResp.success(tdResumeService.selectResume(selectResumeDto));
    }
}
