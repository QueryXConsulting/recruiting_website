package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDJobResume;
import com.queryx.recruiting_website.domain.TDMaterial;
import com.queryx.recruiting_website.domain.TDOffers;
import com.queryx.recruiting_website.domain.dto.MaterialDTO;
import com.queryx.recruiting_website.domain.vo.MaterialStatusVO;
import com.queryx.recruiting_website.mapper.MaterialsMapper;
import com.queryx.recruiting_website.mapper.TDJobResumeMapper;
import com.queryx.recruiting_website.mapper.TDOffersMapper;
import com.queryx.recruiting_website.service.MaterialsService;
import com.queryx.recruiting_website.service.MessageBoardService;
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
    @Autowired
    private TDOffersMapper offersMapper;

    private final String SEND_MESSAGE_CONTENT = "我已经上传了您发布的%s岗位的入职材料，请及时查看。 ——此消息由系统自动发送，请勿回复。";
    @Autowired
    private MessageBoardService messageBoardService;

    @Override
    // TODO 材料上传状态查询: 逻辑待优化
    public CommonResp<MaterialStatusVO> queryProcessStatus() {
        // 获取用户id
        Long userId = SecurityUtils.getLoginUser().getTdUser().getUserId();
        // 查询用户是否到达材料上传环节
        LambdaQueryWrapper<TDJobResume> jrQueryWrapper = new LambdaQueryWrapper<>();
        jrQueryWrapper.select(TDJobResume::getJobResumeId, TDJobResume::getResumeStatus, TDJobResume::getJobId);
        jrQueryWrapper.eq(TDJobResume::getUserId, userId);
        jrQueryWrapper.and((wrapper) -> {
            wrapper.gt(TDJobResume::getResumeStatus, Integer.parseInt(Common.DELIVER_RESUME_STATUS_UPLOAD_MATERIAL));
            wrapper.or();
            wrapper.eq(TDJobResume::getResumeStatus, Common.DELIVER_RESUME_STATUS_UPLOAD_MATERIAL);
        });
        jrQueryWrapper.ne(TDJobResume::getResumeDelete, Common.DELIVER_RESUME_DELETE_SQUARE_PEG);
        final List<TDJobResume> jobResumes = jobResumeMapper.selectList(jrQueryWrapper);
        if (jobResumes == null || jobResumes.isEmpty()) {
            return CommonResp.success(null);
        }
        // 用户只能接受一个offer，所以这里只能有一个
        MaterialStatusVO statusVO = new MaterialStatusVO();
        for (TDJobResume jobResume : jobResumes) {
            int resultStatus = Integer.parseInt(jobResume.getResumeStatus());
            int step = Integer.parseInt(Common.DELIVER_RESUME_STATUS_UPLOAD_MATERIAL);
            // 状态大于该环节，说明已经材料上传审核通过
            if (resultStatus > step) {
                statusVO.setStatus(uploadPassed);
                return new CommonResp<>(AppHttpCodeEnum.SUCCESS.getCode(), Common.MATERIAL_STATUS_THE_UPLOAD_PASSED, statusVO);
            } else if (resultStatus == step) {
                // 判断是否已经上传过材料
                final MaterialDTO materialDTO = materialsMapper.selectOfferAndJobId(userId, Common.DELIVER_RESUME_STATUS_UPLOAD_MATERIAL);
                LambdaQueryWrapper<TDMaterial> materialQueryWrapper = new LambdaQueryWrapper<>();
//                materialQueryWrapper.select(TDMaterial::getMaterialId, TDMaterial::getStatus);
                materialQueryWrapper.eq(TDMaterial::getUserId, userId);
                materialQueryWrapper.eq(TDMaterial::getOfferId, materialDTO.getOffersId());
                materialQueryWrapper.eq(TDMaterial::getCompanyId, materialDTO.getCompanyId());
                materialQueryWrapper.eq(TDMaterial::getJobId, materialDTO.getJobId());
                TDMaterial material = materialsMapper.selectOne(materialQueryWrapper);
                if (material == null) {
                    // 用户第一次上传材料
                    statusVO.setStatus(uploading);
                    return new CommonResp<>(AppHttpCodeEnum.SUCCESS.getCode(), Common.MATERIAL_STATUS_UPLOADING_MATERIAL, statusVO);
                }
                if (material.getMaterialId() == null || material.getStatus() == null) {
                    return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
                }
                switch (material.getStatus()) {
                    case Common.MATERIAL_STATUS_WAIT_REVIEW: // 材料待审核，需要等待审核
                        // 查询材料审核状态
                        if (Common.MATERIAL_STATUS_NOT_PASS.equals(material.getIdentityCardStatus())) {
                            statusVO.setIdentityCardStatus(Common.MATERIAL_STATUS_NOT_PASS);
                        }
                        if (Common.MATERIAL_STATUS_NOT_PASS.equals(material.getPhysicalExaminationStatus())) {
                            statusVO.setPhysicalExaminationStatus(Common.MATERIAL_STATUS_NOT_PASS);
                        }
                        if (Common.MATERIAL_STATUS_NOT_PASS.equals(material.getIdentificationPhotoStatus())) {
                            statusVO.setIdentificationPhotoStatus(Common.MATERIAL_STATUS_NOT_PASS);
                        }
                        if (Common.MATERIAL_STATUS_NOT_PASS.equals(material.getDiplomaStatus())) {
                            statusVO.setDiplomaStatus(Common.MATERIAL_STATUS_NOT_PASS);
                        }
                        if (Common.MATERIAL_STATUS_NOT_PASS.equals(material.getBankCardStatus())) {
                            statusVO.setBankCardStatus(Common.MATERIAL_STATUS_NOT_PASS);
                        }
                        if (Common.MATERIAL_STATUS_NOT_PASS.equals(material.getQualificationCertificateStatus())) {
                            statusVO.setQualificationCertificateStatus(Common.MATERIAL_STATUS_NOT_PASS);
                        }
                        if (Common.MATERIAL_STATUS_NOT_PASS.equals(material.getResignCertificateStatus())) {
                            statusVO.setResignCertificateStatus(Common.MATERIAL_STATUS_NOT_PASS);
                        }
                        if (Common.MATERIAL_STATUS_NOT_PASS.equals(material.getOtherStatus())) {
                            statusVO.setOtherStatus(Common.MATERIAL_STATUS_NOT_PASS);
                        }
                        if (Common.MATERIAL_STATUS_NOT_PASS.equals(material.getSignatureStatus())) {
                            statusVO.setSignatureStatus(Common.MATERIAL_STATUS_NOT_PASS);
                        }
                        statusVO.setOfferId(material.getOfferId());
                        statusVO.setStatus(waitReview);
                        return new CommonResp<>(AppHttpCodeEnum.SUCCESS.getCode(), Common.MATERIAL_STATUS_UPLOAD_SUCCESS, statusVO);
                    case Common.MATERIAL_STATUS_NOT_PASS: // 材料未通过审核，需要重新上传材料
                        LambdaQueryWrapper<TDOffers> queryWrapper = new LambdaQueryWrapper<>();
                        queryWrapper.select(TDOffers::getOfferId);
                        queryWrapper.eq(TDOffers::getUserId, userId);
                        queryWrapper.eq(TDOffers::getJobId, jobResumes.getFirst().getJobId());
                        List<TDOffers> offers = offersMapper.selectList(queryWrapper);
                        if (offers == null || offers.isEmpty()) {
                            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
                        }
                        statusVO.setOfferId(offersMapper.selectList(queryWrapper).getFirst().getOfferId());
                        statusVO.setStatus(reload);
                        return new CommonResp<>(AppHttpCodeEnum.SUCCESS.getCode(), Common.MATERIAL_STATUS_THE_UPLOAD_NOT_PASSED, statusVO);
                    case Common.MATERIAL_STATUS_NOT_SUBMIT: // 用户第一次上传材料
                        statusVO.setStatus(uploading);
                        return new CommonResp<>(AppHttpCodeEnum.SUCCESS.getCode(), Common.MATERIAL_STATUS_UPLOADING_MATERIAL, statusVO);
                    case Common.MATERIAL_STATUS_MATERIAL_PASSED: // 材料审核通过，可以进入下一步
                        statusVO.setStatus(uploadPassed);
                        return new CommonResp<>(AppHttpCodeEnum.SUCCESS.getCode(), Common.MATERIAL_STATUS_THE_UPLOAD_PASSED, statusVO);
                    default:
                }

            } else {
                // 材料上传未开始
                statusVO.setStatus(uploadNotBegin);
                return new CommonResp<>(AppHttpCodeEnum.SUCCESS.getCode(), Common.MATERIAL_STATUS_UPLOAD_NOT_BEGIN, statusVO);
            }
        }
        return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResp<Boolean> insertMaterials(Map<String, MultipartFile> files, Boolean isResetUpload) {
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
            log.warn("材料文件夹创建失败,{}文件夹已存在", folderName);
        }
        Map<String, List<MultipartFile>> map1 = new HashMap<>();
        try {
            String key;
            for (Map.Entry<String, MultipartFile> file : files.entrySet()) {
                key = file.getKey().split("-")[0];
                if (map1.containsKey(key)) { // 存在key
                    map1.get(key).add(file.getValue());
                } else { // 不存在key
                    ArrayList<MultipartFile> multipartFiles = new ArrayList<>();
                    multipartFiles.add(file.getValue());
                    map1.put(key, multipartFiles);
                }
            }
            for (Map.Entry<String, List<MultipartFile>> entry : map1.entrySet()) {
                StringBuilder filePaths = new StringBuilder();
                for (MultipartFile file1 : entry.getValue()) {
                    String fileName = System.currentTimeMillis() + "_" + file1.getOriginalFilename();
                    File target = new File(folder.getAbsolutePath() + File.separator + fileName);
                    file1.transferTo(target.getAbsoluteFile());
                    if (!target.exists()) {
                        log.error("文件上传失败");
                        return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, false);
                    }
                    targetList.add(target.getAbsolutePath());

                    filePaths.append(File.separator).append(folderName).append(fileName).append(Common.MATERIAL_OTHER_STRING_SPLIT);
                }
                // 保存文件路径
                Method method = material.getClass().getDeclaredMethod(entry.getKey(), String.class);
                method.setAccessible(true);
                method.invoke(material, filePaths.toString());
            }

            // 保存到数据库
            material.setStatus(Common.MATERIAL_STATUS_WAIT_REVIEW);
            if (isResetUpload) {
                // 查询材料审核状态
                if (Common.MATERIAL_STATUS_NOT_PASS.equals(material.getIdentityCardStatus())) {
                    material.setIdentityCardStatus(Common.MATERIAL_STATUS_WAIT_REVIEW);
                }
                if (Common.MATERIAL_STATUS_NOT_PASS.equals(material.getPhysicalExaminationStatus())) {
                    material.setPhysicalExaminationStatus(Common.MATERIAL_STATUS_WAIT_REVIEW);
                }
                if (Common.MATERIAL_STATUS_NOT_PASS.equals(material.getIdentificationPhotoStatus())) {
                    material.setIdentificationPhotoStatus(Common.MATERIAL_STATUS_WAIT_REVIEW);
                }
                if (Common.MATERIAL_STATUS_NOT_PASS.equals(material.getDiplomaStatus())) {
                    material.setDiplomaStatus(Common.MATERIAL_STATUS_WAIT_REVIEW);
                }
                if (Common.MATERIAL_STATUS_NOT_PASS.equals(material.getBankCardStatus())) {
                    material.setBankCardStatus(Common.MATERIAL_STATUS_WAIT_REVIEW);
                }
                if (Common.MATERIAL_STATUS_NOT_PASS.equals(material.getQualificationCertificateStatus())) {
                    material.setQualificationCertificateStatus(Common.MATERIAL_STATUS_WAIT_REVIEW);
                }
                if (Common.MATERIAL_STATUS_NOT_PASS.equals(material.getResignCertificateStatus())) {
                    material.setResignCertificateStatus(Common.MATERIAL_STATUS_WAIT_REVIEW);
                }
                if (Common.MATERIAL_STATUS_NOT_PASS.equals(material.getOtherStatus())) {
                    material.setOtherStatus(Common.MATERIAL_STATUS_WAIT_REVIEW);
                }
                if (Common.MATERIAL_STATUS_NOT_PASS.equals(material.getSignatureStatus())) {
                    material.setSignatureStatus(Common.MATERIAL_STATUS_WAIT_REVIEW);
                }
            }
            material.setSendTime(Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai")).getTime());
            boolean result = materialsMapper.insertOrUpdate(material);
            Boolean b = messageBoardService.saveMessage(materialDTO.getCompanyId(), String.format(SEND_MESSAGE_CONTENT, materialDTO.getJobPosition()));
            if (!b) {
                log.error("发送消息失败");
            }
            return CommonResp.success(result);
        } catch (Exception e) {
            log.error("上传文件失败", e);
            for (String filePath : targetList) {
                File file = new File(filePath);
                if (file.exists() || file.delete()) {
                    log.info("删除文件成功：{}", filePath);
                }
            }
            return CommonResp.fail(AppHttpCodeEnum.FILE_UPLOAD_FAIL, false);
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

                file.transferTo(file1.getAbsoluteFile());
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
