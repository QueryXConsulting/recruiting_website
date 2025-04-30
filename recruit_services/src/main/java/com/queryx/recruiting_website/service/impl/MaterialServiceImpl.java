package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.*;
import com.queryx.recruiting_website.domain.vo.MaterialDetailVO;
import com.queryx.recruiting_website.domain.vo.MaterialListVO;
import com.queryx.recruiting_website.mapper.MaterialMapper;
import com.queryx.recruiting_website.mapper.TDJobResumeMapper;
import com.queryx.recruiting_website.mapper.TDOffersMapper;
import com.queryx.recruiting_website.mapper.TDRegistrationMapper;
import com.queryx.recruiting_website.service.*;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, TDMaterial> implements MaterialService {
    @Resource
    private MaterialMapper materialMapper;
    @Resource
    private TDUserService userService;
    @Resource
    private TDRegistrationMapper registrationMapper;
    @Resource
    private TDOffersService offersService;

    @Resource
    private TDJobService jobService;

    @Resource
    private TDJobResumeMapper jobResumeMapper;
    @Resource
    private MessageBoardService messageBoardService;


    @Override
    public Object selectMaterial(Integer page, Integer size, Long jobId) {
        Page<TDMaterial> materialPage = materialMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<TDMaterial>()
                        .eq(TDMaterial::getJobId, jobId));
        if (materialPage.getRecords().isEmpty()) {
            return null;
        }
        List<Long> userList = materialPage.getRecords().stream().map(TDMaterial::getUserId).toList();
        List<TDUser> users = userService.listByIds(userList);
        Map<Long, String> userMap = users.stream().collect(Collectors.toMap(TDUser::getUserId, TDUser::getUserName));
        Page<MaterialListVO> materialListVOPage = new Page<>(materialPage.getCurrent(), materialPage.getSize(), materialPage.getTotal());
        materialListVOPage.setRecords(materialPage.getRecords().stream().map(material -> {
            MaterialListVO materialListVO = new MaterialListVO();
            BeanUtils.copyProperties(material, materialListVO);
            materialListVO.setUserName(userMap.get(material.getUserId()));
            return materialListVO;
        }).toList());
        return materialListVOPage;
    }

    @Override
    public Object selectMaterialDetail(Long materialId, Long offerId) {

        TDMaterial material = getById(materialId);
        MaterialDetailVO materialDetailVO = new MaterialDetailVO();
        BeanUtils.copyProperties(material, materialDetailVO);
        // 取出多个文件路径并拼接
        List<String> bankList = Arrays.stream(material.getBankCard().split("\\|"))
                .map(part -> Common.getBaseURL() + part)
                .toList();
        materialDetailVO.setBankCard(bankList);
        // 取出多个文件路径并拼接
        if (StringUtils.hasText(material.getOther())) {
            List<String> otherList = Arrays.stream(material.getOther().split("\\|"))
                    .map(part -> Common.getBaseURL() + part)
                    .toList();
            materialDetailVO.setOther(otherList);
        }
        String signaturePath = offersService.getById(offerId).getSignaturePath();
        if (StringUtils.hasText(signaturePath)) {
            materialDetailVO.setSignaturePath(Common.getBaseURL() + signaturePath);
        }
        materialDetailVO.setDiploma(Common.getBaseURL() + materialDetailVO.getDiploma());
        List<String> identityCards = Arrays.stream(material.getIdentityCard().split("\\|"))
                .map(part -> Common.getBaseURL() + part)
                .toList();
        materialDetailVO.setIdentityCard(identityCards);
        materialDetailVO.setIdentificationPhoto(Common.getBaseURL() + materialDetailVO.getIdentificationPhoto());
        materialDetailVO.setPhysicalExamination(Common.getBaseURL() + materialDetailVO.getPhysicalExamination());
        if (StringUtils.hasText(materialDetailVO.getQualificationCertificate())) {
            materialDetailVO.setQualificationCertificate(Common.getBaseURL() + materialDetailVO.getQualificationCertificate());
        }
        if (StringUtils.hasText(materialDetailVO.getResignCertificate())) {
            materialDetailVO.setResignCertificate(Common.getBaseURL() + materialDetailVO.getResignCertificate());
        }
        return materialDetailVO;
    }

    @Override
    public Object updateMaterialStatus(Long materialId, String status) {
        if (status.equals("1")) {
            TDMaterial material = getById(materialId);
            TDOffers offer = offersService.getById(material.getOfferId());
            TDJob job = jobService.getById(offer.getJobId());
            TDUser user = userService.getById(offer.getUserId());

            TDRegistration tdRegistration = new TDRegistration();
            tdRegistration.setJobId(offer.getJobId());
            tdRegistration.setPhoneNumber(user.getUserPhone());
            tdRegistration.setUserName(user.getUserName());
            tdRegistration.setEmail(user.getUserEmail());
            tdRegistration.setOfferId(material.getOfferId());
            tdRegistration.setPosition(job.getJobPosition());
            registrationMapper.insert(tdRegistration);

            LambdaUpdateWrapper<TDJobResume> tdJobResumeLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            tdJobResumeLambdaUpdateWrapper.eq(TDJobResume::getJobId, material.getJobId())
                    .eq(TDJobResume::getUserId, material.getUserId()).set(TDJobResume::getResumeStatus, "6");
            jobResumeMapper.update(tdJobResumeLambdaUpdateWrapper);
            TDMaterial tdMaterial = getById(materialId);

            messageBoardService.sendMessage(tdMaterial.getUserId(), "您的材料已审核通过 ——此消息来自系统自动发送");
        }
        update(new LambdaUpdateWrapper<TDMaterial>().eq(TDMaterial::getMaterialId, materialId)
                .set(TDMaterial::getStatus, status));
        return null;
    }
}
