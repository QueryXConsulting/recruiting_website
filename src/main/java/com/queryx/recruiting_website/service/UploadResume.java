package com.queryx.recruiting_website.service;

import com.queryx.recruiting_website.utils.CommonResp;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadResume {
    /**
     * 处理上传简历
     *
     * @param userId 用户id
     * @param file   简历文件
     * @return 上传结果
     * @throws IOException 异常
     */
    Integer handleUploadResume(Long userId, MultipartFile file) throws IOException;
}
