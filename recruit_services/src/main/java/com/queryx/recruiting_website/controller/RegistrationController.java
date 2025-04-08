package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.dto.RegistrationDTO;
import com.queryx.recruiting_website.domain.vo.RegistrationStatusVO;
import com.queryx.recruiting_website.domain.vo.InfoInputVO;
import com.queryx.recruiting_website.domain.vo.ReservationRegistrationVO;
import com.queryx.recruiting_website.service.RegistrationService;
import com.queryx.recruiting_website.utils.CommonResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * @Author：fjj
 * @Date：2025/3/12 9:09
 */
@RestController
@RequestMapping("/registration")
@Tag(name = "信息录入预约报到", description = "管理候选人信息的录入及报到流程")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }


    @GetMapping("/status/query")
    @Operation(summary = "信息录入状态查询", responses = {
            @ApiResponse(responseCode = "200", description = "上传成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "512", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    public CommonResp<RegistrationStatusVO> getRegistrationStatus() {
        return registrationService.queryRegistrationStatus();
    }


    @GetMapping("/position")
    @Operation(summary = "查询入职职位", description = "查询信息录入页面职位", responses = {
            @ApiResponse(responseCode = "200", description = "上传成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "512", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    public CommonResp<InfoInputVO> getInfoInputPosition() {
        return registrationService.queryInfoInputPosition();
    }


    @PostMapping("/submit")
    @Operation(summary = "信息录入提交", parameters = {
            @Parameter(name = "registrationDTO", description = "候选人信息", schema = @Schema(implementation = RegistrationDTO.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "上传成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "512", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    public CommonResp<Boolean> insertRegistrationInfo(@RequestBody RegistrationDTO registrationDTO) {
        if (registrationDTO == null) {
            return CommonResp.fail(AppHttpCodeEnum.MISSING_PARAMETERS, null);
        }
        return registrationService.insertRegistrationInfo(registrationDTO);
    }


    @GetMapping("/reservation")
    @Operation(summary = "查询候选人信息", description = "查询候选人信息", responses = {
            @ApiResponse(responseCode = "200", description = "上传成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "512", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    public CommonResp<ReservationRegistrationVO> getReservationRegistrationInfo() {
        return registrationService.queryRegistrationInfo();
    }


    @PutMapping("/status/update")
    @Operation(summary = "修改报到状态", parameters = {
            @Parameter(name = "status", description = "状态", schema = @Schema(implementation = String.class), required = true)
    }, description = "修改候选人报到状态", responses = {
            @ApiResponse(responseCode = "200", description = "上传成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "512", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    public CommonResp<Boolean> setReservationRegistrationStatus(@RequestBody String status) {
        return registrationService.insertReservationStatus(status);
    }
}
