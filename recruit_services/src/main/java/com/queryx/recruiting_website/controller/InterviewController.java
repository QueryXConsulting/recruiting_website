package com.queryx.recruiting_website.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.vo.InterviewDateVO;
import com.queryx.recruiting_website.domain.vo.InterviewVO;
import com.queryx.recruiting_website.service.InterviewService;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/interview")
public class InterviewController {

    @Autowired
    private InterviewService interviewService;

    /**
     * 查询用户面试信息
     *
     * @param interviewId 面试id
     * @return 面试信息
     */
    @Operation(summary = "查询用户面试信息", parameters = {
            @Parameter(name = "interviewId", description = "面试id", schema = @Schema(implementation = Long.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "477", description = "面试不存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "512", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @GetMapping("/info")
    public CommonResp<InterviewVO> queryInterview(@RequestParam("interviewId") Long interviewId) {
        return interviewService.getInterview(interviewId);
    }


    /**
     * 查询面试时间段
     *
     * @param companyId 公司id
     * @return 面试信息
     */
    @Operation(summary = "查询面试时间段", parameters = {
            @Parameter(name = "companyId", description = "公司id", schema = @Schema(implementation = Long.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "512", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @GetMapping("/date")
    public CommonResp<List<InterviewDateVO>> getInterviewDate(@RequestParam("companyId") Long companyId) {
        return interviewService.getInterviewDate(companyId);
    }

    /**
     * 查询用户所有面试信息
     *
     * @param page     页码
     * @param pageSize 页大小
     * @return 面试信息
     */
    @Operation(summary = "查询用户面试信息", parameters = {
            @Parameter(name = "page", description = "当前页码", schema = @Schema(implementation = Integer.class), required = true),
            @Parameter(name = "pageSize", description = "一页显示的条数", schema = @Schema(implementation = Integer.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "512", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @GetMapping("/list")
    public CommonResp<Page<InterviewVO>> queryInterviews(@RequestParam("page") Integer page, @RequestParam("pageSize") Integer pageSize) {
        return interviewService.getInterviews(SecurityUtils.getLoginUser().getTdUser().getUserId(), page, pageSize);
    }


    /**
     * 更新面试状态
     *
     * @param interviewId 面试id
     * @param isAccept    是否接受
     * @return 是否成功
     */
    @Operation(summary = "更新面试状态", parameters = {
            @Parameter(name = "interviewId", description = "面试id", schema = @Schema(implementation = Long.class), required = true),
            @Parameter(name = "isAccept", description = "是否接受", schema = @Schema(implementation = String.class), required = true),
            @Parameter(name = "date", description = "接受之后约定的面试时间", schema = @Schema(implementation = Date.class), required = false)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "接受成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "477", description = "面试不存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "482", description = "缺少参数", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "485", description = "用户不存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "512", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @PutMapping("/isAccept")
    public CommonResp<Boolean> handleAcceptInterview(@RequestParam("interviewId") Long interviewId, @RequestParam("isAccept") String isAccept, @RequestBody(required = false) Date date) {
        if (interviewId == null || isAccept == null) {
            return CommonResp.fail(AppHttpCodeEnum.MISSING_PARAMETERS, null);
        }
        return interviewService.isAcceptInterview(interviewId, isAccept, date);
    }


}
