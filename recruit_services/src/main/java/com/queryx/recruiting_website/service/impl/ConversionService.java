package com.queryx.recruiting_website.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.OfferTemplates;
import com.queryx.recruiting_website.domain.TDOffers;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.OfferTemplateMapper;
import com.queryx.recruiting_website.mapper.TDOffersMapper;
import jakarta.annotation.Resource;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class ConversionService {

    @Resource
    private TDOffersMapper offersMapper;

    public Object uploadOffer(MultipartFile pdfFile, String offerId) {
        long currentTimeMillis = System.currentTimeMillis();
        String pdfName = currentTimeMillis + "_" + offerId + ".pdf";
        String pdfPath = Common.officeFilePath + pdfName;
        File pdfDestFile = new File(pdfPath);
        try {
            pdfFile.transferTo(pdfDestFile.getAbsoluteFile());
        } catch (IOException e) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        TDOffers offers = new TDOffers();
        offers.setOfferId(Long.valueOf(offerId));
        offers.setOffersFilePath("/offer_files/" + pdfName);
        offersMapper.updateById(offers);
        return true;
    }


}
