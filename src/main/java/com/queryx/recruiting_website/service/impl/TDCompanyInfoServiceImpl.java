package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDCompanyInfo;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDCompanyInfoMapper;
import com.queryx.recruiting_website.service.TDCompanyInfoService;
import com.queryx.recruiting_website.domain.vo.CompanyInfoDto;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TDCompanyInfoServiceImpl extends ServiceImpl<TDCompanyInfoMapper, TDCompanyInfo> implements TDCompanyInfoService {
    @Resource
    private TDCompanyInfoMapper tdCompanyInfoMapper;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public CompanyInfoDto selectCompanyInfo(Long companyId) {
        LambdaQueryWrapper<TDCompanyInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TDCompanyInfo::getCompanyInfoId, companyId)
                .eq(TDCompanyInfo::getCompanyInfoStatus, Common.STATUS_ENABLE.getCode())
                .eq(TDCompanyInfo::getCompanyInfoReview, Common.REVIEW_SUCCESS.getCode());
        TDCompanyInfo tdCompanyInfo = tdCompanyInfoMapper.selectOne(lambdaQueryWrapper);
        CompanyInfoDto companyInfoDto = new CompanyInfoDto();
        BeanUtils.copyProperties(tdCompanyInfo, companyInfoDto);
        return companyInfoDto;
    }

    @Override
    public CompanyInfoDto updateCompanyInfo(CompanyInfoDto companyInfoDto) {
        if (!StringUtils.hasText(companyInfoDto.getCompanyInfoName())) {
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }
        if (!StringUtils.hasText(companyInfoDto.getCompanyInfoPassword())) {
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if (!StringUtils.hasText(companyInfoDto.getCompanyInfoUsername())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }

        LambdaQueryWrapper<TDCompanyInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TDCompanyInfo::getCompanyInfoUsername, companyInfoDto.getCompanyInfoUsername());
        if (count(queryWrapper) > 0) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }

        TDCompanyInfo tdCompanyInfo = new TDCompanyInfo();
        BeanUtils.copyProperties(companyInfoDto, tdCompanyInfo);
        tdCompanyInfo.setCompanyInfoPassword(passwordEncoder.encode(tdCompanyInfo.getCompanyInfoPassword()));
        UpdateWrapper<TDCompanyInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("company_info_id", companyInfoDto.getCompanyInfoId());
        tdCompanyInfo.setCompanyInfoStatus(Common.STATUS_DISABLE.getCode());
        tdCompanyInfo.setCompanyInfoReview(Common.REVIEW_WAIT.getCode());
        if (!update(tdCompanyInfo, updateWrapper)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }
}
