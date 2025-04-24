package com.queryx.recruiting_website.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDJobResume;
import com.queryx.recruiting_website.domain.TDOffers;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.dto.OfferDataDto;
import com.queryx.recruiting_website.domain.vo.OfferTemplateVO;
import com.queryx.recruiting_website.domain.vo.OffersVO;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.OfferTemplateMapper;
import com.queryx.recruiting_website.mapper.TDJobResumeMapper;
import com.queryx.recruiting_website.mapper.TDOffersMapper;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import com.queryx.recruiting_website.service.MessageBoardService;
import com.queryx.recruiting_website.service.TDOffersService;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.PDFFormUtils;
import jakarta.annotation.Resource;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
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
    @Resource
    private MessageBoardService messageBoardService;


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
        // 数据转化
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
            String fileName = System.currentTimeMillis() + "_" + offerId + ".pdf";
            String officeName = "/offer_files/" + fileName;
            offer.setOffersFilePath(officeName);
            update(offer, new LambdaUpdateWrapper<TDOffers>().eq(TDOffers::getOfferId, offerId));
            // 下载文件并保存为 PDF
            try {
                downloadFileFromUrl(fileUrl, Common.officeFilePath + fileName);
                return ResponseEntity.ok("{\"error\":0, \"message\":\"保存成功\"}");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.ok("{\"error\":1, \"message\":\"保存失败\"}");
            }
        }
        return ResponseEntity.ok("{\"error\":0}");
    }

    @Override
    public CommonResp updateOfferStatus(Long offerId, String status, Long jobId, Long userId) {
        LambdaUpdateWrapper<TDOffers> offersLambdaUpdateWrapper = new LambdaUpdateWrapper<TDOffers>();
        offersLambdaUpdateWrapper.eq(TDOffers::getOfferId, offerId)
                .set(TDOffers::getOffersStatus, status);
        if (status.equals("6")) {
            // 更新简历流程状态
            LambdaUpdateWrapper<TDJobResume> resumeLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            resumeLambdaUpdateWrapper
                    .eq(TDJobResume::getUserId, userId)
                    .eq(TDJobResume::getJobId, jobId)
                    .set(TDJobResume::getResumeStatus, "4");
            jobResumeMapper.update(resumeLambdaUpdateWrapper);
            messageBoardService.sendMessage(userId, "offer已通过,请上传材料 —此消息来自系统自动发送");
        } else if (Common.OFFER_STATUS_SEND.equals(status)) {
            offersLambdaUpdateWrapper.set(TDOffers::getOffersDate, new Date());
        }

        if (offersMapper.update(offersLambdaUpdateWrapper) < 0) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public String saveOffer(OfferDataDto offerDataDto) throws IOException {
        Map<String, String> fieldValues = new HashMap<>();
        // 动态生成表单域值映射
        fieldValues.put("userName", offerDataDto.getUserName()); // 姓名
        fieldValues.put("workTime", offerDataDto.getWorkTime()); // 工作时间
        fieldValues.put("salary", offerDataDto.getSalary()); // 工资待遇
        fieldValues.put("company", offerDataDto.getCompanyName()); // 公司名称
        fieldValues.put("company_1", offerDataDto.getCompanyName()); // 公司名称
        fieldValues.put("position", offerDataDto.getPosition()); // 职位
        fieldValues.put("welfare", offerDataDto.getWelfare()); // 福利
        fieldValues.put("workLocation", offerDataDto.getWorkLocation()); // 工作地点
        fieldValues.put("material", offerDataDto.getMaterial()); // 报道材料
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        fieldValues.put("date", currentDate.getYear() + "年"
                + currentDate.getMonthValue() + "月"
                + currentDate.getDayOfMonth() + "日");


        // 填充处理
        String templatePath = offerDataDto.getTemplatePath();
        String fileName = templatePath.substring(templatePath.lastIndexOf('/') + 1);
        PDDocument pdDocument = PDFFormUtils.fillPDFForm(Common.officeTemplatePath + fileName, fieldValues, null, null);
        fileName = System.currentTimeMillis() + "_" + offerDataDto.getOfferId() + ".pdf";
        String officeName = "/offer_files/" + fileName;
        pdDocument.save(Common.officeFilePath + fileName);
        pdDocument.close();
        update(new LambdaUpdateWrapper<TDOffers>().eq(TDOffers::getOfferId, offerDataDto.getOfferId())
                .set(TDOffers::getOffersFilePath, officeName));
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
