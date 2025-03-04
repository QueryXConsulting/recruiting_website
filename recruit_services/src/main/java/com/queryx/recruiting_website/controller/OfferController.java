package com.queryx.recruiting_website.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.vo.OffersVO;
import com.queryx.recruiting_website.service.OfferService;
import com.queryx.recruiting_website.utils.CommonResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author：fjj
 * @Date：2025/3/3 15:50
 */
@RestController
@RequestMapping("/offer")
@Tag(name = "offer", description = "offer管理")
public class OfferController {

    @Autowired
    private OfferService offerService;




    @Operation(summary = "offer列表", parameters = {
            @Parameter(name = "page", description = "页面", schema = @Schema(implementation = Integer.class), required = true),
            @Parameter(name = "size", description = "条数", schema = @Schema(implementation = Integer.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "上传成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @RequestMapping("/list")
    public CommonResp<Page<OffersVO>> queryOffers(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        if (page == null || size == null) return CommonResp.fail(AppHttpCodeEnum.MISSING_PARAMETERS, null);
        return offerService.getOffers(page, size);
    }


    @Operation(summary = "offer详情", parameters = {
            @Parameter(name = "offerId", description = "offerId", schema = @Schema(implementation = Long.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "上传成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "482", description = "缺少参数", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "486", description = "offer不存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @GetMapping("/detail")
    public CommonResp<String> queryOfferFilePath(@RequestParam("offerId") Long offerId) {
        if (offerId == null) return CommonResp.fail(AppHttpCodeEnum.MISSING_PARAMETERS, null);
        return offerService.getOfferFilePath(offerId);
    }


    @Operation(summary = "修改offer状态", parameters = {
            @Parameter(name = "offerId", description = "offerId", schema = @Schema(implementation = Long.class), required = true),
            @Parameter(name = "status", description = "offer状态", schema = @Schema(implementation = String.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "上传成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "486", description = "offer不存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @PutMapping("/status")
    public CommonResp<Boolean> setOfferStatus(@RequestParam("offerId") Long offerId, @RequestParam("status") String status) {
        if (offerId == null || status == null) return CommonResp.fail(AppHttpCodeEnum.MISSING_PARAMETERS, null);
        return offerService.setOfferStatus(offerId, status);
    }




    @Operation(summary = "签名图片上传", parameters = {
            @Parameter(name = "offerId", description = "offerId", schema = @Schema(implementation = Long.class), required = true),
            @Parameter(name = "image", description = "签名图片", schema = @Schema(implementation = MultipartFile.class), required = true),
    }, responses = {
            @ApiResponse(responseCode = "200", description = "上传成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "482", description = "缺少参数", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "486", description = "offer不存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @PutMapping("/signature")
    public CommonResp<Boolean> updateSignature(@RequestParam("offerId") Long offerId, @RequestParam("image") MultipartFile file) {
        if (offerId == null || file == null) return CommonResp.fail(AppHttpCodeEnum.MISSING_PARAMETERS, null);
        return offerService.updateSignature(offerId, file);
    }
}
