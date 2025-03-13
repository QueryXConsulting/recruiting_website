package com.queryx.recruiting_website.service.impl;


import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.OfferTemplates;
import com.queryx.recruiting_website.domain.TDOffers;
import com.queryx.recruiting_website.mapper.OfferTemplateMapper;
import com.queryx.recruiting_website.mapper.TDOffersMapper;
import com.queryx.recruiting_website.service.TDOffersService;
import jakarta.annotation.Resource;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class OnlyOfficeConversionService {

    @Resource
    private OfferTemplateMapper offerTemplateMapper;

    public Object convertPdfToImage(MultipartFile pdfFile,String offerId) throws IOException {
        String pdfFilename = pdfFile.getOriginalFilename();
        File pdfDestFile = new File(pdfFilename);
        pdfFile.transferTo(pdfDestFile);
      // TODO 待续


        return null;


    }


}
