package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.dto.DeliverResumeDTO;
import com.queryx.recruiting_website.domain.vo.JobResumeVO;
import com.queryx.recruiting_website.service.DeliverService;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/deliver")
@Tag(name = "简历投递", description = "简历投递相关接口")
public class DeliverController {

    @Autowired
    private DeliverService deliverService;

    /**
     * 简历投递
     *
     * @param deliverResumeDTO 简历投递DTO
     * @return 成功或失败的响应
     */
    @Operation(summary = "简历投递", parameters = {
            @Parameter(name = "deliverResumeDTO", description = "简历投递信息", schema = @Schema(implementation = DeliverResumeDTO.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(type = "object", implementation = CommonResp.class))),
            @ApiResponse(responseCode = "421", description = "投递简历失败", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", implementation = CommonResp.class)))
    })
    @PostMapping("/resume")
    public CommonResp<Integer> deliverResume(@RequestBody DeliverResumeDTO deliverResumeDTO) {
        int count;
        try {
            count = deliverService.insertDeliverResume(deliverResumeDTO);
            if (count <= 0) {
                log.error("简历投递失败，简历id={}", deliverResumeDTO.getResumeId());
                return CommonResp.fail(AppHttpCodeEnum.DELIVER_RESUME_FAIL, null);
            }
        } catch (Exception e) {
            log.error("简历投递错误，{}", e.getMessage());
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        log.info("简历投递成功");
        return CommonResp.success(count);
    }

    public CommonResp<List<JobResumeVO>> queryJobResumeList(@RequestParam("page") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        if (pageNum == null || pageSize == null) return CommonResp.fail(AppHttpCodeEnum.PAGINATION_NOT_NULL, null);
        final Long userId = SecurityUtils.getLoginUser().getTdUser().getUserId();
        List<JobResumeVO> jobResumeList = deliverService.queryJobResumeList(userId, pageNum, pageSize);
        return CommonResp.success(jobResumeList);
    }
}
