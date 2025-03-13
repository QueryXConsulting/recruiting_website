package com.queryx.recruiting_website.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDJobResume;
import com.queryx.recruiting_website.domain.TDOffers;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.vo.OfferTemplateVO;
import com.queryx.recruiting_website.domain.vo.OffersVO;
import com.queryx.recruiting_website.mapper.OfferTemplateMapper;
import com.queryx.recruiting_website.mapper.TDJobResumeMapper;
import com.queryx.recruiting_website.mapper.TDOffersMapper;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import com.queryx.recruiting_website.service.TDOffersService;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TDOffersServiceImpl extends ServiceImpl<TDOffersMapper, TDOffers> implements TDOffersService {

    @Resource
    private TDOffersMapper offersMapper;
    @Resource
    private TDUserMapper userMapper;
    @Resource
    private OfferTemplateMapper offerTemplateMapper;
    @Resource
    private TDJobResumeMapper jobResumeMapper;


    @Override
    public Object offerList(Integer page, Integer size, Long jobId) {
        LambdaQueryWrapper<TDOffers> tdOffersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tdOffersLambdaQueryWrapper.eq(TDOffers::getJobId, jobId);
        Page<TDOffers> tdOffersPage = offersMapper.selectPage(new Page<>(page, size), tdOffersLambdaQueryWrapper);
        if (tdOffersPage.getRecords().isEmpty()) {
            return null;
        }
        List<Long> userIds = tdOffersPage.getRecords().stream().map(TDOffers::getUserId).toList();
        List<Map<String, Object>> maps = userMapper.selectMaps(new LambdaQueryWrapper<TDUser>()
                .in(TDUser::getUserId, userIds)
                .select(TDUser::getUserId, TDUser::getUserName));
        Map<Long, String> userMap = maps.stream()
                .collect(Collectors.toMap(
                        map -> (Long) map.get("user_id"),
                        map -> map.get("user_name").toString()
                ));

        Page<OffersVO> offersVOPage = new Page<>(tdOffersPage.getCurrent(), tdOffersPage.getSize(), tdOffersPage.getTotal());
        offersVOPage.setRecords(tdOffersPage.getRecords().stream().map(tdOffer -> {
            OffersVO offersVO = new OffersVO();
            BeanUtils.copyProperties(tdOffer, offersVO);
            offersVO.setUserName(userMap.get(tdOffer.getUserId()));
            offersVO.setSignaturePath(Common.getImgURL() + offersVO.getSignaturePath());
            return offersVO;
        }).toList());
        return offersVOPage;
    }

    @Override
    public Object selectOfferTemplate() {
        return offerTemplateMapper.selectList(null).stream().map(offerTemplate -> {
            OfferTemplateVO offerTemplateVO = new OfferTemplateVO();
            BeanUtils.copyProperties(offerTemplate, offerTemplateVO);
            offerTemplateVO.setTemplateImg(Common.getImgURL() + offerTemplateVO.getTemplateImg());
            offerTemplateVO.setTemplateFilePath(Common.getImgURL() + offerTemplateVO.getTemplateFilePath());
            return offerTemplateVO;
        }).toList();
    }

    @Override
    public ResponseEntity<String> saveOfferDocument(Long offerId, Map<String, Object> callbackData) {
        Integer status = (Integer) callbackData.get("status");
        if (status == 2) {
            TDOffers offer = getById(offerId);
            String fileUrl = (String) callbackData.get("url");
            if (fileUrl == null) {
                return ResponseEntity.ok("{\"error\":1, \"message\":\"保存失败\"}");
            }
            String officeName = "/offer_files/" + System.currentTimeMillis() + "_" + offerId + ".pdf";

            offer.setOffersFilePath(officeName);
            update(offer, new LambdaUpdateWrapper<TDOffers>().eq(TDOffers::getOffersId, offerId));
            // 下载文件并保存为 PDF
            try {
                downloadFileFromUrl(fileUrl, Common.officeFilePath + System.currentTimeMillis() + "_" + offerId + ".pdf");
                return ResponseEntity.ok("{\"error\":0, \"message\":\"保存成功\"}");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.ok("{\"error\":1, \"message\":\"保存失败\"}");
            }
        }
        return ResponseEntity.ok("{\"error\":0}");
    }

    @Override
    public CommonResp updateOfferStatus(Long offerId, String status, Long jobId) {
        if (status.equals("1")) {
            // 更新简历流程状态
            LambdaUpdateWrapper<TDJobResume> resumeLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            resumeLambdaUpdateWrapper
                    .eq(TDJobResume::getUserId, SecurityUtils.getLoginUser().getTdUser().getUserId())
                    .eq(TDJobResume::getJobId, jobId)
                    .set(TDJobResume::getResumeStatus, "4");
            jobResumeMapper.update(resumeLambdaUpdateWrapper);

        }
        offersMapper.update(new LambdaUpdateWrapper<TDOffers>().eq(TDOffers::getOffersId, offerId)
                .set(TDOffers::getOffersStatus, status));
        return null;
    }


    private void downloadFileFromUrl(String fileUrl, String outputPdfPath) throws Exception {
        try (InputStream in = new URL(fileUrl).openStream();
             FileOutputStream out = new FileOutputStream(outputPdfPath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

}
