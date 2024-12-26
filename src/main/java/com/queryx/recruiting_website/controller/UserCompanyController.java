package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.service.TDCompanyInfoService;
import com.queryx.recruiting_website.service.TDJobService;
import com.queryx.recruiting_website.service.TDResumeService;
import com.queryx.recruiting_website.service.TDUserService;
import com.queryx.recruiting_website.utils.CommonResp;

import com.queryx.recruiting_website.vo.CompanyInfoVo;
import com.queryx.recruiting_website.vo.JobDetailVo;
import com.queryx.recruiting_website.vo.JobInsertVo;
import com.queryx.recruiting_website.vo.UserCompanyVo;
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
    public CommonResp updateJob(@RequestBody JobDetailVo jobDetailVo) {
        return CommonResp.success(tdJobService.updateJob(jobDetailVo));
    }

    @PostMapping("/publishJob/{companyId}")
    public CommonResp insertJobInfo(@RequestBody JobInsertVo jobInsertVo, @PathVariable("companyId") Long companyId) {
        return CommonResp.success(tdJobService.insertJobInfo(jobInsertVo, companyId));
    }

    @GetMapping("/selectUserInfo")
    public CommonResp selectUserInfo(Long userId, String userRole) {
        return CommonResp.success(tdUserService.selectUserInfo(userId, userRole));
    }
    @PostMapping("/updateUserInfo")
    public CommonResp updateUserInfo( @RequestBody UserCompanyVo userCompanyVo) {
        return CommonResp.success(tdUserService.updateUserInfo(userCompanyVo));
    }

    @GetMapping("/selectCompanyInfo/{companyId}")
    public CommonResp selectCompanyInfo(@PathVariable("companyId") Long companyId) {
        return CommonResp.success(tdCompanyInfoService.selectCompanyInfo(companyId));
    }

    @PostMapping("/updateCompanyInfo")
    public CommonResp updateCompanyInfo(@RequestBody CompanyInfoVo companyInfoVo) {
        return CommonResp.success(tdCompanyInfoService.updateCompanyInfo(companyInfoVo));
    }

    @GetMapping("/resumeList/{companyId}")
    public CommonResp selectResumeList(@PathVariable("companyId") Long companyId) {
        return CommonResp.success(tdResumeService.insertJobInfo(companyId));
    }
}
