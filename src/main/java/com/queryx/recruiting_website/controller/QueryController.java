package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.vo.JobVO;
import com.queryx.recruiting_website.service.impl.QueryImpl;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.domain.vo.AttachmentsResumeVO;
import com.queryx.recruiting_website.domain.vo.InterviewVO;
import com.queryx.recruiting_website.domain.vo.ResumeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/query")
@Tag(name = "查询模块", description = "用户查询相关接口")
public class QueryController {

    @Autowired
    private QueryImpl queryUserInfo;

    /**
     * 查询用户在线简历
     *
     * @param id 简历id
     * @return 在线简历信息
     */
    @Operation(summary = "查询用户在线简历", parameters = {
            @Parameter(name = "id", description = "简历id", schema = @Schema(implementation = Long.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "423", description = "简历不存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @GetMapping("/resume/online")
    public CommonResp<ResumeVO> queryOnlineResume(@RequestParam("id") Long id) {
        ResumeVO resp;
        try {
            resp = queryUserInfo.getOnlineResume(id);
            if (resp == null) {
                log.error("用户在线简历不存在，id={}", id);
                return CommonResp.fail(AppHttpCodeEnum.RESUME_NOT_EXIST, null);
            }
        } catch (Exception e) {
            log.error("用户在线简历查询失败，{}", e.getMessage());
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        return CommonResp.success(resp);
    }

    /**
     * 查询用户所有附件简历
     *
     * @param id 用户id
     * @return 附件简历列表
     * TODO: 附件简历列表查询：上传数量限制
     */
    @Operation(summary = "查询用户所有附件简历", parameters = {
            @Parameter(name = "id", description = "用户id", schema = @Schema(implementation = Long.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "423", description = "简历不存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @GetMapping("/resume/attachments")
    public CommonResp<List<AttachmentsResumeVO>> queryResumeAttachments(@RequestParam("id") Long id) {
        List<AttachmentsResumeVO> resp = null;
        try {
            resp = queryUserInfo.getResumeAttachmentList(id);
            if (resp == null) {
                return CommonResp.fail(AppHttpCodeEnum.RESUME_NOT_EXIST, null);
            }
        } catch (Exception e) {
            log.error("用户附件简历查询失败，{}", e.getMessage());
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, resp);
        }
        return CommonResp.success(resp);
    }

    /**
     * 查询用户面试信息
     *
     * @param id 用户id
     * @return 面试信息
     */
    @Operation(summary = "查询用户面试信息", parameters = {
            @Parameter(name = "id", description = "用户id", schema = @Schema(implementation = Long.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "425", description = "面试不存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @GetMapping("/interview")
    public CommonResp<InterviewVO> queryReviewResume(@RequestParam("id") Long id) {
        InterviewVO resp;
        try {
            resp = queryUserInfo.getInterview(id);
            if (resp == null) {
                log.error("用户面试信息不存在，id={}", id);
                return CommonResp.fail(AppHttpCodeEnum.INTERVIEW_NOT_EXIST, null);
            }
        } catch (Exception e) {
            log.error("用户面试信息查询失败，{}", e.getMessage());
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        return CommonResp.success(resp);
    }

    /**
     * 查询招聘岗位信息
     *
     * @param id 岗位id
     * @return 招聘岗位信息
     */
    @Operation(summary = "查询招聘岗位信息", parameters = {
            @Parameter(name = "id", description = "岗位id", schema = @Schema(implementation = Long.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "416", description = "招聘岗位不存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @GetMapping("jobs")
    public CommonResp<JobVO> queryJob(@RequestParam("id") Long id) {
        JobVO resp;
        try {
            resp = queryUserInfo.getJob(id);
            if (resp == null) {
                log.error("招聘岗位不存在，id={}", id);
                return CommonResp.fail(AppHttpCodeEnum.JOB_NOT_EXIST, null);
            }
        } catch (Exception e) {
            log.error("招聘岗位未知错误，{}", e.getMessage());
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        return CommonResp.success(resp);
    }
}
