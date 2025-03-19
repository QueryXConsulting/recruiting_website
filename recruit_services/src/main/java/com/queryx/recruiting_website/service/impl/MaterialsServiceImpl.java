package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDJobResume;
import com.queryx.recruiting_website.domain.TDMaterial;
import com.queryx.recruiting_website.domain.dto.MaterialDTO;
import com.queryx.recruiting_website.mapper.MaterialsMapper;
import com.queryx.recruiting_website.mapper.TDJobResumeMapper;
import com.queryx.recruiting_website.service.MaterialsService;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Author：fjj
 * @Date：2025/3/6 16:17
 */
@Slf4j
@Service
public class MaterialsServiceImpl implements MaterialsService {

    // 材料文件夹前缀
    private static final String MATERIAL_FOLDER_PREFIX = "material_";
    // 其他材料文件前缀
    private static final String OTHER_MATERIAL_PREFIX = "other_";

    private static final int uploadNotBegin = -1;// 材料上传未开始
    private static final int uploadPassed = 0;// 材料上传通过
    private static final int reload = 2;// 材料重新上传(审核未通过)
    private static final int uploading = 1;// 正在材料上传
    private static final int waitReview = 3;// 材料待审核

    @Value("${file.upload-path-material}")
    private String uploadPath;

    @Autowired
    private TDJobResumeMapper jobResumeMapper;
    @Autowired
    private MaterialsMapper materialsMapper;

    @Override
    public CommonResp<Integer> queryProcessStatus() {
        // 获取用户id
        Long userId = SecurityUtils.getLoginUser().getTdUser().getUserId();
        // 查询
        LambdaQueryWrapper<TDJobResume> jrQueryWrapper = new LambdaQueryWrapper<>();
        jrQueryWrapper.select(TDJobResume::getJobResumeId, TDJobResume::getResumeStatus);
        jrQueryWrapper.eq(TDJobResume::getUserId, userId);
        jrQueryWrapper.ne(TDJobResume::getResumeDelete, Common.DELIVER_RESUME_DELETE_SQUARE_PEG);
        TDJobResume jobResume = jobResumeMapper.selectOne(jrQueryWrapper);
        int resultStatus = Integer.parseInt(jobResume.getResumeStatus());
        int step = Integer.parseInt(Common.DELIVER_RESUME_STATUS_UPLOAD_MATERIAL);
        // 状态大于该环节，说明已经材料上传审核通过
        if (resultStatus > step) {
            return new CommonResp<>(AppHttpCodeEnum.SUCCESS.getCode(), Common.MATERIAL_STATUS_THE_UPLOAD_PASSED, uploadPassed);
        } else if (resultStatus == step) {
            // 判断是否已经上传过材料
            final MaterialDTO materialDTO = materialsMapper.selectOfferAndJobId(userId, Common.DELIVER_RESUME_STATUS_UPLOAD_MATERIAL);
            LambdaQueryWrapper<TDMaterial> materialQueryWrapper = new LambdaQueryWrapper<>();
            materialQueryWrapper.select(TDMaterial::getMaterialId, TDMaterial::getStatus);
            materialQueryWrapper.eq(TDMaterial::getUserId, userId);
            materialQueryWrapper.eq(TDMaterial::getOfferId, materialDTO.getOffersId());
            materialQueryWrapper.eq(TDMaterial::getCompanyId, materialDTO.getCompanyId());
            materialQueryWrapper.eq(TDMaterial::getJobId, materialDTO.getJobId());
            TDMaterial material = materialsMapper.selectOne(materialQueryWrapper);
            if (material == null) {
                // 用户第一次上传材料
                return new CommonResp<>(AppHttpCodeEnum.SUCCESS.getCode(), Common.MATERIAL_STATUS_UPLOADING_MATERIAL, uploading);
            }
            if (material.getMaterialId() == null || material.getStatus() == null) {
                return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
            }
            switch (material.getStatus()) {
                case Common.MATERIAL_STATUS_WAIT_REVIEW: // 材料待审核，需要等待审核
                    return new CommonResp<>(AppHttpCodeEnum.SUCCESS.getCode(), Common.MATERIAL_STATUS_UPLOAD_SUCCESS, waitReview);
                case Common.MATERIAL_STATUS_NOT_PASS: // 材料未通过审核，需要重新上传材料
                    return new CommonResp<>(AppHttpCodeEnum.SUCCESS.getCode(), Common.MATERIAL_STATUS_THE_UPLOAD_NOT_PASSED, reload);
                case Common.MATERIAL_STATUS_NOT_SUBMIT: // 用户第一次上传材料
                    return new CommonResp<>(AppHttpCodeEnum.SUCCESS.getCode(), Common.MATERIAL_STATUS_UPLOADING_MATERIAL, uploading);
                default:
            }

        } else {
            // 材料上传未开始
            return new CommonResp<>(AppHttpCodeEnum.SUCCESS.getCode(), Common.MATERIAL_STATUS_UPLOAD_NOT_BEGIN, uploadNotBegin);
        }
        return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResp<Boolean> insertMaterials(Map<String, MultipartFile> files) {
        ArrayList<String> targetList = new ArrayList<>();// 保存文件名

        final Long userId = SecurityUtils.getLoginUser().getTdUser().getUserId();
        final MaterialDTO materialDTO = materialsMapper.selectOfferAndJobId(userId, Common.DELIVER_RESUME_STATUS_UPLOAD_MATERIAL);
        LambdaQueryWrapper<TDMaterial> materialQueryWrapper = new LambdaQueryWrapper<>();
        materialQueryWrapper.eq(TDMaterial::getUserId, userId);
        materialQueryWrapper.eq(TDMaterial::getOfferId, materialDTO.getOffersId());
        materialQueryWrapper.eq(TDMaterial::getCompanyId, materialDTO.getCompanyId());
        materialQueryWrapper.eq(TDMaterial::getJobId, materialDTO.getJobId());
        TDMaterial material = materialsMapper.selectOne(materialQueryWrapper);

        if (material == null) {
            material = new TDMaterial();
            material.setOfferId(materialDTO.getOffersId());
            material.setUserId(userId);
            material.setCompanyId(materialDTO.getCompanyId());
            material.setJobId(materialDTO.getJobId());
        }
        // 保存文件
        String folderName = MATERIAL_FOLDER_PREFIX + userId;
        File folder = new File(uploadPath + folderName);

        // 创建文件夹
        if (!folder.mkdirs()) { // 文件夹已存在
            log.warn("创建文件夹失败,{}文件夹已存在", folderName);
        }
        try {
            for (Map.Entry<String, MultipartFile> file : files.entrySet()) {
                MultipartFile file1 = file.getValue();
                String fileName = System.currentTimeMillis() + "_" + file1.getOriginalFilename();
                File target = new File(folder.getAbsolutePath() + File.separator + fileName);
                file1.transferTo(target);
                if (!target.exists()) {
                    log.error("文件上传失败");
                    return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, false);
                }
                targetList.add(target.getAbsolutePath());
                // 保存文件路径
                Method method = material.getClass().getDeclaredMethod(file.getKey(), String.class);
                method.setAccessible(true);

                method.invoke(material, File.separator + folderName + File.separator + fileName);
            }
            // 保存到数据库
            material.setStatus(Common.MATERIAL_STATUS_WAIT_REVIEW);
            material.setSendTime(Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai")).getTime());
            boolean result = materialsMapper.insertOrUpdate(material);
            return CommonResp.success(result);
        } catch (Exception e) {
            log.error("上传文件失败", e);
            for (String filePath : targetList) {
                File file = new File(filePath);
                if (file.exists() || file.delete()) {
                    log.info("删除文件成功：{}", filePath);
                }
            }
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, false);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResp<Boolean> insertOtherMaterials(List<MultipartFile> files) {
        final Long userId = SecurityUtils.getLoginUser().getTdUser().getUserId();
        final MaterialDTO materialDTO = materialsMapper.selectOfferAndJobId(userId, Common.DELIVER_RESUME_STATUS_UPLOAD_MATERIAL);

        LambdaQueryWrapper<TDMaterial> materialQueryWrapper = new LambdaQueryWrapper<>();
        materialQueryWrapper.eq(TDMaterial::getUserId, userId);
        materialQueryWrapper.eq(TDMaterial::getOfferId, materialDTO.getOffersId());
        materialQueryWrapper.eq(TDMaterial::getCompanyId, materialDTO.getCompanyId());
        materialQueryWrapper.eq(TDMaterial::getJobId, materialDTO.getJobId());
        TDMaterial material = materialsMapper.selectOne(materialQueryWrapper);
        if (material.getMaterialId() == null) {
            return CommonResp.fail(AppHttpCodeEnum.REQUIRED_MATERIALS_WERE_NOT_UPLOADED, false);
        }
        // 获得文件夹名
        int start = material.getBankCard().indexOf(File.separator);
        int end = material.getBankCard().indexOf(File.separator, start + 1);
        String folderName = material.getBankCard().substring(start + 1, end + 1);// 结尾包含文件分隔符
        StringBuffer buffer = new StringBuffer();
        try {
            String fileName = null;
            for (MultipartFile file : files) {
                fileName = OTHER_MATERIAL_PREFIX + System.currentTimeMillis() + "_" + file.getOriginalFilename();
                File file1 = new File(uploadPath + folderName + fileName);

                file.transferTo(file1);
                if (!file1.exists()) {
                    log.error("其他材料上传失败，用户id：{}", material.getMaterialId());
                    return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, false);
                }
                buffer.append(File.separator).append(folderName).append(fileName).append(Common.MATERIAL_OTHER_STRING_SPLIT);
            }
            // 保存文件路径到数据库
            material.setOther(buffer.toString());
            int result = materialsMapper.updateById(material);
            return CommonResp.success(result > 0);
        } catch (IOException e) {
            log.error("上传文件失败", e);
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, false);
        }
    }


}
