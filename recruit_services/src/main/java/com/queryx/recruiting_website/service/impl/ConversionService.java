package com.queryx.recruiting_website.service.impl;


import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.OfferTemplates;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.OfferTemplateMapper;
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
    private OfferTemplateMapper offerTemplateMapper;

    public Object convertPdfToImage(MultipartFile pdfFile, String offerId) throws IOException {
        long currentTimeMillis = System.currentTimeMillis();
        String pdfName = currentTimeMillis + "_" + offerId + ".pdf";
        String pdfPath = Common.officeTemplatePath + pdfName;
        File pdfDestFile = new File(pdfPath);
        pdfFile.transferTo(pdfDestFile);
        // 加载 PDF 文档
        try (PDDocument document = Loader.loadPDF(pdfDestFile)) {
            String img = currentTimeMillis + "_" + offerId + ".png";
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            String imageDir = Common.uploadPath + img;
            String imgName = "/avatar_files/" + img;
            if (document.getNumberOfPages() > 1) {
                throw new SystemException(AppHttpCodeEnum.FILE_PDF_PAGE);
            }

            // 渲染页面为图像
            BufferedImage bim = pdfRenderer.renderImageWithDPI(document.getNumberOfPages() - 1, 720, ImageType.RGB);
            // 保存图像到文件
            File imageFile = new File(imageDir);
            boolean png = ImageIO.write(bim, "png", imageFile);
            if (png) {
                String officeName = "/offer_Template/" + pdfName;
                OfferTemplates offerTemplates = new OfferTemplates();
                offerTemplates.setTemplateImg(imgName);
                offerTemplates.setTemplateFilePath(officeName);
                offerTemplates.setTemplateName(currentTimeMillis + "_" + offerId);
                offerTemplateMapper.insert(offerTemplates);
            } else {
                throw new SystemException(AppHttpCodeEnum.FILE_UPLOAD_FAIL);
            }

        }

        return null;
    }


}
