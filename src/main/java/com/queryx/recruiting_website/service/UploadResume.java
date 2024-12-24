package com.queryx.recruiting_website.service;

import com.queryx.recruiting_website.utils.CommonResp;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadResume {
    Integer handleUploadResume(Long userId, MultipartFile file) throws IOException;
}
