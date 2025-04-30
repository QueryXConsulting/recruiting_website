package com.queryx.recruiting_website.service;

import com.queryx.recruiting_website.domain.vo.MaterialStatusVO;
import com.queryx.recruiting_website.utils.CommonResp;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @Author：fjj
 * @Date：2025/3/6 16:13
 */
public interface MaterialsService {

    /**
     * 查询是否有流程是否到达材料上传
     * @return 结果
     */
    CommonResp<MaterialStatusVO> queryProcessStatus();

    /**
     * 插入材料
     * @param file 材料文件
     * @param isResetUpload 是否重置上传
     * @return 结果
     */
    CommonResp<Boolean> insertMaterials(Map<String, MultipartFile> file, Boolean isResetUpload);


    /**
     * 插入其他材料
     * @param file 材料文件
     * @return 结果
     */
    CommonResp<Boolean> insertOtherMaterials(List<MultipartFile> file);


}
