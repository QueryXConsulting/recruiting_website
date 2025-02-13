package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDCompanyInfo;
import com.queryx.recruiting_website.domain.dto.UserRegisterDTO;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDCompanyInfoMapper;
import com.queryx.recruiting_website.service.TDCompanyInfoService;
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
    public UserRegisterDTO.CompanyInfoDTO selectCompanyInfo(Long companyId) {
        LambdaQueryWrapper<TDCompanyInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TDCompanyInfo::getCompanyInfoId, companyId)
                .eq(TDCompanyInfo::getCompanyInfoStatus, Common.STATUS_ENABLE)
                .eq(TDCompanyInfo::getCompanyInfoReview, Common.REVIEW_OK);
        TDCompanyInfo tdCompanyInfo = tdCompanyInfoMapper.selectOne(lambdaQueryWrapper);
        UserRegisterDTO.CompanyInfoDTO companyInfoDTO = new UserRegisterDTO.CompanyInfoDTO();
        BeanUtils.copyProperties(tdCompanyInfo, companyInfoDTO);
        return companyInfoDTO;
    }

    @Override
    public UserRegisterDTO.CompanyInfoDTO updateCompanyInfo(UserRegisterDTO.CompanyInfoDTO companyInfoDTO) {
        if (!StringUtils.hasText(companyInfoDTO.getCompanyInfoName())) {
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }
        if (!StringUtils.hasText(companyInfoDTO.getCompanyInfoPassword())) {
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if (!StringUtils.hasText(companyInfoDTO.getCompanyInfoUsername())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }

        LambdaQueryWrapper<TDCompanyInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TDCompanyInfo::getCompanyInfoUsername, companyInfoDTO.getCompanyInfoUsername());
        if (count(queryWrapper) > 0) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }

        TDCompanyInfo tdCompanyInfo = new TDCompanyInfo();
        BeanUtils.copyProperties(companyInfoDTO, tdCompanyInfo);
        tdCompanyInfo.setCompanyInfoPassword(passwordEncoder.encode(tdCompanyInfo.getCompanyInfoPassword()));
        UpdateWrapper<TDCompanyInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("company_info_id", companyInfoDTO.getCompanyInfoId());
        tdCompanyInfo.setCompanyInfoStatus(Common.STATUS_DISABLE);
        tdCompanyInfo.setCompanyInfoReview(Common.REVIEW_WAIT);
        if (!update(tdCompanyInfo, updateWrapper)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }
}
