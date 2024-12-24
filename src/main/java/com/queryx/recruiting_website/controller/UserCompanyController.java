package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.service.TDJobService;
import com.queryx.recruiting_website.service.TDResumeService;
import com.queryx.recruiting_website.service.TDUserService;
import com.queryx.recruiting_website.utils.CommonResp;

import com.queryx.recruiting_website.vo.JobDetailVo;
import com.queryx.recruiting_website.vo.JobInsertVo;
import com.queryx.recruiting_website.vo.UserVo;
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

    @GetMapping("/jobList")
    public CommonResp selectJobList(Integer page, Integer size, Long companyId){
        return CommonResp.success(tdJobService.selectJobList(page,size,companyId));
    }

    @GetMapping("/jobInfo/{jobId}")
    public CommonResp selectJobInfo(@PathVariable("jobId") Long jobId){
       return CommonResp.success(tdJobService.selectJobInfo(jobId));
    }

    @PostMapping("/updateJob")
    public CommonResp updateJob(@RequestBody JobDetailVo jobDetailVo){
        return CommonResp.success(tdJobService.updateJob(jobDetailVo));
    }

    @PostMapping("/publishJob/{companyId}")
    public CommonResp insertJobInfo(@RequestBody JobInsertVo jobInsertVo, @PathVariable("companyId") Long companyId){
        return CommonResp.success(tdJobService.insertJobInfo(jobInsertVo,companyId));
    }



}
