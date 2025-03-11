package com.queryx.recruiting_website.service;

import com.queryx.recruiting_website.utils.CommonResp;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author：fjj
 * @Date：2025/3/6 16:13
 */
public interface MaterialsService {

    CommonResp<String> testUpload(MultipartFile file);
}
