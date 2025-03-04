package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.queryx.recruiting_website.domain.vo.OffersVO;
import com.queryx.recruiting_website.utils.CommonResp;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author：fjj
 * @Date：2025/3/3 16:01
 */
public interface OfferService {

    /**
     * 获取offer列表
     *
     * @param page 页码
     * @param size 每页大小
     * @return offer列表
     */
    CommonResp<Page<OffersVO>> getOffers(Integer page, Integer size);

    /**
     * 修改offer状态
     *
     * @param offerId offerId
     * @param status  状态
     * @return 是否成功
     */
    CommonResp<Boolean> setOfferStatus(Long offerId, String status);

    /**
     *  预览offer
     * @Param offerId
     * @return offer文件路径
     */
    CommonResp<String> getOfferFilePath(Long offerId);

    /**
     * 上传手写签名图片
     *
     * @param offerId offerId
     * @param file    签名图片文件
     * @return offer文件路径
     */
    CommonResp<Boolean> updateSignature(Long offerId, MultipartFile file);
}
