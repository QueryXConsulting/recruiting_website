package com.queryx.recruiting_website.controller;


import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.dto.SelectResumeDto;
import com.queryx.recruiting_website.service.TDResumeService;
import com.queryx.recruiting_website.service.UserResumeService;
import com.queryx.recruiting_website.utils.CommonResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin")
@Tag(name = "管理员模块下简历管理")
public class ResumeManageController {

    @Resource
    private TDResumeService resumeService;
    @Resource
    private UserResumeService userResumeService;

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


    @Operation(summary = "删除用户附件简历", parameters = {
            @Parameter(name = "id", description = "附件简历ID", schema = @Schema(type = "Long", format = "int64"), example = "1", required = true)
    },
            responses = {
                    @ApiResponse(responseCode = "200", description = "删除成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
                    @ApiResponse(responseCode = "469", description = "删除失败", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
                    @ApiResponse(responseCode = "512", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
            })
    @DeleteMapping("/delete")
    public CommonResp<Integer> handleResumeDelete(@RequestParam("id") Long raId) {
        int deletedRows = 0;
        try {
            deletedRows = userResumeService.deleteResumeAttachment(raId);
            if (deletedRows <= 0) {
                log.error("删除简历失败，简历ID为：{}", raId);
                return CommonResp.fail(AppHttpCodeEnum.DELETE_RESUME_ERROR, deletedRows);
            }
        } catch (Exception e) {
            log.error("无法删除ID为: {}的简历附件", raId, e);
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, deletedRows);
        }
        return CommonResp.success(deletedRows);
    }


}
