package com.queryx.recruiting_website.service.impl;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.service.MaterialsService;
import com.queryx.recruiting_website.utils.CommonResp;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author：fjj
 * @Date：2025/3/6 16:17
 */
@Service
public class MaterialsServiceImpl implements MaterialsService {
    @Override
    public CommonResp<String> testUpload(MultipartFile file) {
        if (file == null) {
            return CommonResp.fail(AppHttpCodeEnum.FILE_UPLOAD_FAIL, "文件名为空");
        }
        return null;
    }
}
