package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.dto.ResumeDTO;
import com.queryx.recruiting_website.service.UserResumeService;
import com.queryx.recruiting_website.utils.CommonResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 用户简历管理
 */
@Slf4j
@RestController
@RequestMapping("/resume")
public class UserResumeController {

    @Autowired
    private UserResumeService userManagementService;

    /**
     * 上传附件简历
     *
     * @param userId 用户ID
     * @param file   上传的文件
     * @return 返回上传结果
     */
    @PostMapping("/upload")
    public CommonResp<Integer> handleResumeUpload(@RequestParam("id") Long userId, @RequestParam("file") MultipartFile file) {
        Integer message = 0;
        try {
            message = userManagementService.insertResumeAttachment(userId, file);
            if (message <= 0) throw new IOException();
            return CommonResp.success(message);
        } catch (IOException e) {
            log.error("附件简历上传失败: {}", e.getMessage());
            return CommonResp.fail(AppHttpCodeEnum.FILE_UPLOAD_ERROR, message);
        }
    }

    /**
     * 删除用户附件简历
     *
     * @param raId 简历ID
     * @return 返回删除结果
     */
    @DeleteMapping("/delete")
    public CommonResp<Integer> handleResumeDelete(@RequestParam("id") Long raId) {
        Integer row = 0;
        try {
            row = userManagementService.deleteResumeAttachment(raId);
            if (row <= 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            return CommonResp.fail(AppHttpCodeEnum.DELETE_RESUME_ERROR, row);
        }
        return CommonResp.success(row);
    }

    /**
     * 更新用户在线简历信息
     *
     * @param resumeDTO 简历信息
     * @return 返回更新结果
     */
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
