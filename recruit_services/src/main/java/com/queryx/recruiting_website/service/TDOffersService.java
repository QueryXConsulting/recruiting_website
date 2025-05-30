package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.queryx.recruiting_website.domain.TDOffers;
import com.queryx.recruiting_website.domain.dto.OfferDataDto;
import com.queryx.recruiting_website.utils.CommonResp;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;


public interface TDOffersService extends IService<TDOffers> {


    Object offerList(Integer page, Integer size, Long jobId);

    Object selectOfferTemplate();

    ResponseEntity<String> saveOfferDocument(Long offerId, Map<String, Object> callbackData) throws IOException;


    CommonResp updateOfferStatus(Long offerId, String status, Long jobId, Long userId);


    String saveOffer(OfferDataDto offerDataDto) throws IOException;
}

