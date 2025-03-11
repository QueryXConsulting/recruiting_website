package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.service.MaterialsService;
import com.queryx.recruiting_website.utils.CommonResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @Author：fjj
 * @Date：2025/3/6 16:10
 */
@RestController
@RequestMapping("/materials")
public class MaterialsController {

    private final MaterialsService materialsService;

    // 构造方法属性注入
    public MaterialsController(MaterialsService materialsService) {
        this.materialsService = materialsService;
    }



        @GetMapping("/status")
    @Operation(summary = "是否到达该流程", responses = {
            @ApiResponse(responseCode = "200", description = "上传成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    public CommonResp<Integer> queryIsProcessArrives() {
        return materialsService.queryProcessStatus();
    }



    @PostMapping("/upload")
    @Operation(summary = "材料上传", parameters = {
            @Parameter(name = "files", description = "所有上传文件", schema = @Schema(implementation = List.class), required = true),
    }, responses = {
            @ApiResponse(responseCode = "200", description = "上传成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    public CommonResp<Boolean> uploadMaterials(@RequestParam() Map<String , MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return CommonResp.fail(AppHttpCodeEnum.MISSING_PARAMETERS, null);
        }
        return materialsService.insertMaterials(files);
    }


    @PostMapping("/upload/other")
    @Operation(summary = "其他材料上传", parameters = {
            @Parameter(name = "files", description = "所有上传文件", schema = @Schema(implementation = List.class), required = true),
    }, responses = {
            @ApiResponse(responseCode = "200", description = "上传成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    public CommonResp<Boolean> uploadOtherMaterials(@RequestParam("files") List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return CommonResp.fail(AppHttpCodeEnum.MISSING_PARAMETERS, null);
        }
        return materialsService.insertOtherMaterials(files);
    }







}
