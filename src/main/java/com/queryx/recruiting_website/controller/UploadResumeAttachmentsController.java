package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.service.UploadResume;
import com.queryx.recruiting_website.utils.CommonResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadResumeAttachmentsController {

    @Autowired
    private UploadResume uploadResume;

    @PostMapping("/resume")
    public CommonResp<Integer> handleResumeUpload(@RequestParam("id") Long userId, @RequestParam("file") MultipartFile file) {
        Integer message = 0;
        try {
            message = uploadResume.handleUploadResume(userId, file);
            if (message <= 0) throw new IOException();
            return new CommonResp<>(
                    AppHttpCodeEnum.SUCCESS.getCode(),
                    AppHttpCodeEnum.SUCCESS.getMsg(),
                    message);
        } catch (IOException e) {
            log.error("附件简历上传失败: {}", e.getMessage());
            return new CommonResp<>(
                    AppHttpCodeEnum.FILE_UPLOAD_ERROR.getCode(),
                    AppHttpCodeEnum.FILE_UPLOAD_ERROR.getMsg(),
                    message);
        }

    }
}
