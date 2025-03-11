package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.*;
import com.queryx.recruiting_website.domain.vo.OffersVO;
import com.queryx.recruiting_website.mapper.TDCompanyInfoMapper;
import com.queryx.recruiting_website.mapper.TDJobMapper;
import com.queryx.recruiting_website.mapper.TDJobResumeMapper;
import com.queryx.recruiting_website.mapper.TDOffersMapper;
import com.queryx.recruiting_website.service.OfferService;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @Author：fjj
 * @Date：2025/3/3 16:02
 */
@Slf4j
@Service
public class OfferServiceImpl implements OfferService {

    @Value("${file.upload-path-signature}")
    private String signaturePath;

    @Autowired
    private TDOffersMapper offerMapper;

    @Autowired
    private TDJobMapper jobMapper;
    @Autowired
    private TDJobResumeMapper jobResumeMapper;
    @Autowired
    private TDCompanyInfoMapper companyInfoMapper;


    @Override
    public CommonResp<Page<OffersVO>> getOffers(Integer page, Integer size) {
        final TDUser user = SecurityUtils.getLoginUser().getTdUser();
        LambdaQueryWrapper<TDOffers> offerQueryWrapper = new LambdaQueryWrapper<>();
        offerQueryWrapper.eq(TDOffers::getUserId, user.getUserId());
        Page<TDOffers> selectPage = offerMapper.selectPage(new Page<>(page, size), offerQueryWrapper);
        if (selectPage == null) {
            log.warn("查询offer失败, 用户id: {}", user.getUserId());
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        Page<OffersVO> resultPage = new Page<>(selectPage.getCurrent(), selectPage.getSize(), selectPage.getTotal());
        ArrayList<OffersVO> list = new ArrayList<>();
        for (TDOffers offer : selectPage.getRecords()) {
            OffersVO vo = new OffersVO();
            BeanUtils.copyProperties(offer, vo);
            TDJob tdJob = jobMapper.selectOne(new LambdaQueryWrapper<TDJob>()
                    .select(TDJob::getJobPosition, TDJob::getCompanyId)
                    .eq(TDJob::getJobId, vo.getJobId()));
            vo.setJobPosition(tdJob.getJobPosition());

            TDCompanyInfo companyInfo = companyInfoMapper.selectOne(new LambdaQueryWrapper<TDCompanyInfo>()
                    .select(TDCompanyInfo::getCompanyInfoName)
                    .eq(TDCompanyInfo::getCompanyInfoId, tdJob.getCompanyId()));
            vo.setCompanyInfoName(companyInfo.getCompanyInfoName());
            list.add(vo);
        }
        resultPage.setRecords(list);
        return CommonResp.success(resultPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResp<Boolean> setOfferStatus(Long offerId, String status) {
        // 修改offer表状态
        LambdaQueryWrapper<TDOffers> offersQueryWrapper = new LambdaQueryWrapper<>();
        offersQueryWrapper.eq(TDOffers::getOfferId, offerId);
        TDOffers offer = offerMapper.selectOne(offersQueryWrapper);
        if (offer == null) {
            log.warn("offerId: {} 不存在", offerId);
            return CommonResp.fail(AppHttpCodeEnum.OFFER_NOT_EXIST, false);
        }
        offer.setOffersStatus(status);
        int ui = offerMapper.updateById(offer);
        if (ui <= 0) {
            log.warn("offerId: {} 修改状态失败", offerId);
            throw new RuntimeException("offerId: " + offerId + " 修改状态失败");
        }
        if (Common.OFFER_STATUS_REJECT.equals(status)) {
            TDJobResume jobResume = jobResumeMapper.selectOne(new LambdaQueryWrapper<TDJobResume>()
                    .select(TDJobResume::getJobResumeId, TDJobResume::getResumeStatus, TDJobResume::getResumeDelete)
                    .eq(TDJobResume::getJobId, offer.getJobId())
                    .eq(TDJobResume::getUserId, offer.getUserId()));
            if (jobResume == null) {
                log.warn("offerId: {} 对应的job_resume不存在", offerId);
                return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, false);
            }
            // 流程到此处，说明offer被拒绝
            jobResume.setResumeStatus(Common.DELIVER_RESUME_STATUS_OFFER);// 流程改为offer发放
            jobResume.setResumeDelete(Common.DELIVER_RESUME_DELETE_SQUARE_PEG);// 状态为不合适
            int ui2 = jobResumeMapper.updateById(jobResume);
            if (ui2 <= 0) {
                log.warn("offerId: {} 对应的job_resume修改状态失败", offerId);
                throw new RuntimeException("offerId: " + offerId + " 对应的job_resume修改状态失败");
            }
        }
        return CommonResp.success(true);
    }


    @Override
    public CommonResp<String> getOfferFilePath(Long offerId){
        String offersFilePath = offerMapper.selectById(offerId).getOffersFilePath();
        if (offersFilePath == null) {
            log.warn("offerId: {} 对应的文件不存在", offerId);
            return CommonResp.fail(AppHttpCodeEnum.OFFER_NOT_EXIST, null);
        }
        return CommonResp.success(Common.getImgURL() + offersFilePath);
    }


    @Override
    public CommonResp<Boolean> updateSignature(Long offerId, MultipartFile file) {
        TDOffers offers = offerMapper.selectById(offerId);
        if (offers == null) {
            log.warn("offer不存在，offerId: {}", offerId);
            return CommonResp.fail(AppHttpCodeEnum.OFFER_NOT_EXIST, false);
        }
        if (offers.getSignaturePath() != null) {
            String folderPath = Common.getUploadFolderPath(signaturePath, "/") + offers.getSignaturePath();
            File file1 = new File(folderPath);
            if (!(file1.exists() && file1.delete())) {
                log.warn("删除旧的签名失败，offerId: {}, 文件路径: {}", offerId, folderPath);
                return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, false);
            }
        }
        String newFileName = System.currentTimeMillis() + file.getOriginalFilename();
        // 保存文件
        try {
            file.transferTo(new File(signaturePath + newFileName));
        } catch (IOException e) {
            log.error("上传签名失败 offerId: {} ", offerId, e);
            File file1 = new File(newFileName);
            if (!(file1.exists() || file1.delete())) {
                log.warn("删除临时文件失败，文件名: {}", newFileName);
            }
            throw new RuntimeException(e);
        }
        newFileName =  "/" + newFileName;
        offers.setSignaturePath(Common.getUploadFolderName(signaturePath, "/", newFileName));
        int ui = offerMapper.updateById(offers);
        if (ui <= 0) {
            log.warn("offerId: {} 更新签名失败", offerId);
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, false);
        }
        return CommonResp.success(true);
    }


}
