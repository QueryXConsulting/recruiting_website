package com.queryx.recruiting_website.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.dto.SelectResumeDTO;
import com.queryx.recruiting_website.domain.vo.ResumeManageVO;
import com.queryx.recruiting_website.service.TDResumeService;
import com.queryx.recruiting_website.utils.CommonResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Tag(name = "管理员模块下简历管理")
public class ResumeManageController {

    @Resource
    private TDResumeService resumeService;

    @GetMapping("/selectResumeList")
    @Operation(summary = "简历列表")
    public CommonResp selectResumeList(Integer page, Integer size, String userName, String resumeReview, String resumeStatus, String resumeType) {
        return CommonResp.success(resumeService.selectResumeManage(page, size, userName, resumeReview, resumeStatus, resumeType));
    }

    @PostMapping("/selectResumeInfo")
    @Operation(summary = "简历信息")
    public CommonResp selectResumeInfo(@RequestBody SelectResumeDto selectResumeDto) {
        return CommonResp.success(resumeService.selectResume(selectResumeDto));
    }


    @GetMapping("/resumeReview/{review}/{resumeId}/{resumeType}")
    @Operation(summary = "简历审核")
    public CommonResp resumeReview(@PathVariable("review") String review, @PathVariable("resumeId") Long resumeId, @PathVariable("resumeType") String resumeType) {
        return CommonResp.success(resumeService.resumeReview(review, resumeId, resumeType));
    }


}
