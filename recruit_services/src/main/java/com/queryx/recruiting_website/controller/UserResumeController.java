package com.queryx.recruiting_website.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.queryx.recruiting_website.domain.TDResumeAttachments;
import com.queryx.recruiting_website.mapper.TDResumeAttachmentsMapper;
import com.queryx.recruiting_website.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.queryx.recruiting_website.utils.CommonResp;
import org.springframework.web.multipart.MultipartFile;
import com.queryx.recruiting_website.domain.dto.ResumeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.service.UserResumeService;

/**
 * 用户简历管理
 */
@Slf4j
@RestController
@RequestMapping("/resume")
@Tag(name = "用户简历管理", description = "用户简历管理")
public class UserResumeController {

    @Autowired
    private UserResumeService userManagementService;

    @Autowired
    private TDResumeAttachmentsMapper tdResumeAttachmentsMapper;
    /**
     * 上传附件简历
     *
     * @param file   上传的文件
     * @return 返回上传结果
     */
    @Operation(summary = "上传附件简历", parameters = {
            @Parameter(name = "attachment", description = "上传的文件", schema = @Schema(implementation = MultipartFile.class), required = true)
    },
    responses = {
            @ApiResponse(responseCode = "200", description = "上传成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "457", description = "文件上传失败", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "512", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @PostMapping("/upload")
    public CommonResp<Integer> handleResumeUpload(@RequestParam("attachment") MultipartFile file) {
        Integer message = 0;
        final Long userId = SecurityUtils.getLoginUser().getTdUser().getUserId();
        LambdaQueryWrapper<TDResumeAttachments> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(TDResumeAttachments::getResumeAttachmentId);
        wrapper.eq(TDResumeAttachments::getUserId, userId);
        // 附件简历数量限制
        if (tdResumeAttachmentsMapper.selectCount(wrapper) > 5) {
            return CommonResp.fail(AppHttpCodeEnum.NUMBER_OF_ATTACHED_RESUMES_EXCEEDS_THE_LIMIT, null);
        }
        try {
            message = userManagementService.insertResumeAttachment(userId, file);
            if (message <= 0) return CommonResp.fail(AppHttpCodeEnum.FILE_UPLOAD_ERROR, message);
            return CommonResp.success(message);
        } catch (Exception e) {
            log.error("附件简历上传失败: {}", e.getMessage());
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, message);
        }
    }

    /**
     * 删除用户附件简历
     *
     * @param raId 附件简历ID
     * @return 返回删除结果
     */
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
            deletedRows = userManagementService.deleteResumeAttachment(raId);
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


    /**
     * 更新用户在线简历信息
     *
     * @param resumeDTO 简历信息
     * @return 返回更新结果
     */
    @Operation(summary = "更新用户在线简历信息", parameters = {
            @Parameter(name = "resumeDTO", description = "简历信息", schema = @Schema(type = "object", implementation = ResumeDTO.class), required = true)
    },
    responses = {
            @ApiResponse(responseCode = "200", description = "更新成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "468", description = "更新失败", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @PutMapping("/update")
    public CommonResp<Integer> handleResumeUpdate(@RequestBody ResumeDTO resumeDTO) {
        Integer row = 0;
        try {
            row = userManagementService.updateResume(resumeDTO);
            if (row <= 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            return CommonResp.fail(AppHttpCodeEnum.UPDATE_RESUME_ERROR, row);
        }
        return CommonResp.success(row);
    }
}
