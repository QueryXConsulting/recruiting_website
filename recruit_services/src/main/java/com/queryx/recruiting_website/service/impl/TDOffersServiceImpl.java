package com.queryx.recruiting_website.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDOffers;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.vo.OfferTemplateVO;
import com.queryx.recruiting_website.domain.vo.OffersVO;
import com.queryx.recruiting_website.mapper.OfferTemplateMapper;
import com.queryx.recruiting_website.mapper.TDOffersMapper;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import com.queryx.recruiting_website.service.TDOffersService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
}
