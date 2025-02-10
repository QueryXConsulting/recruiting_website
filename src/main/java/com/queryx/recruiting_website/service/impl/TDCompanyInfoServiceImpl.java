package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDCompanyInfo;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.vo.CompanyInfoDto;
import com.queryx.recruiting_website.domain.vo.CompanyInfoVO;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDCompanyInfoMapper;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import com.queryx.recruiting_website.service.TDCompanyInfoService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TDCompanyInfoServiceImpl extends ServiceImpl<TDCompanyInfoMapper, TDCompanyInfo> implements TDCompanyInfoService {
    @Resource
    private TDCompanyInfoMapper tdCompanyInfoMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private TDUserMapper userMapper;

    @Value("${file.upload-path-avatar}")
    private String uploadPath;
    @Value("${server.port}")
    private String port;
    @Value("${server.address}")
    private String ip;


    @Override
    public CompanyInfoDto selectCompanyInfo(Long companyId) {
        LambdaQueryWrapper<TDCompanyInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TDCompanyInfo::getCompanyInfoId, companyId)
                .eq(TDCompanyInfo::getCompanyInfoStatus, Common.STATUS_ENABLE);
        TDCompanyInfo tdCompanyInfo = tdCompanyInfoMapper.selectOne(lambdaQueryWrapper);
        CompanyInfoDto companyInfoDto = new CompanyInfoDto();
        BeanUtils.copyProperties(tdCompanyInfo, companyInfoDto);
        companyInfoDto.setCompanyLogo("http://" + ip + ":" + port + companyInfoDto.getCompanyLogo());
        return companyInfoDto;
    }

    @Override
    public CompanyInfoDto updateCompanyInfo(CompanyInfoDto companyInfoDto, MultipartFile applyFiles) {
        // TODO 公司资质附件功能上传(待续)
        TDCompanyInfo tdCompanyInfo = new TDCompanyInfo();
        BeanUtils.copyProperties(companyInfoDto, tdCompanyInfo);
        if (StringUtils.hasText(companyInfoDto.getCompanyInfoPassword())) {
            tdCompanyInfo.setCompanyInfoPassword(passwordEncoder.encode(tdCompanyInfo.getCompanyInfoPassword()));
        }


        UpdateWrapper<TDCompanyInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("company_info_id", companyInfoDto.getCompanyInfoId());
        tdCompanyInfo.setCompanyInfoStatus(companyInfoDto.getCompanyInfoStatus());
        tdCompanyInfo.setCompanyInfoReview(companyInfoDto.getCompanyInfoReview());

        if (!applyFiles.getOriginalFilename().equals(Common.FILE_EMPTY)) {
            String fileName = applyFiles.getOriginalFilename();
            long currentTimeMillis = System.currentTimeMillis();
            if (!isAllowedFileType(getFileExtension(fileName))) {
                throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
            }

            try {
                File uploadDir = new File(uploadPath);
                File destFile = new File(uploadDir, currentTimeMillis + "_" + fileName);
                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }
                applyFiles.transferTo(destFile);
                tdCompanyInfo.setCompanyLogo("/avatar_files/" + currentTimeMillis + "_" + fileName);
                // 删除旧的图片
                String oldCompanyLogoURL = getById(companyInfoDto.getCompanyInfoId()).getCompanyLogo();
                int lastIndex = oldCompanyLogoURL.lastIndexOf('/');
                String file = oldCompanyLogoURL.substring(lastIndex + 1);
                File oldFile = new File(uploadPath + file);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new SystemException(AppHttpCodeEnum.FILE_UPLOAD_ERROR);
            }
        }

        if (!update(tdCompanyInfo, updateWrapper)) {
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        String[] parts = fileName.split("\\.");
        if (parts.length > 1) {
            return parts[parts.length - 1].toLowerCase(); // 返回扩展名并转为小写
        }
        return "";
    }

    private boolean isAllowedFileType(String fileExtension) {
        String[] allowedExtensions = {"jpg", "png"};
        for (String ext : allowedExtensions) {
            if (ext.equals(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object selectCompanyInfoList(Integer page, Integer size, String companyStatus, String companyReview, String companyName) {
        LambdaQueryWrapper<TDCompanyInfo> companyQueryWrapper = new LambdaQueryWrapper<>();
        companyQueryWrapper.like(StringUtils.hasText(companyName), TDCompanyInfo::getCompanyInfoName, companyName)
                .eq(StringUtils.hasText(companyStatus), TDCompanyInfo::getCompanyInfoStatus, companyStatus)
                .eq(StringUtils.hasText(companyReview), TDCompanyInfo::getCompanyInfoReview, companyReview);

        Page<TDCompanyInfo> tdCompanyInfoPage = tdCompanyInfoMapper.selectPage(new Page<>(page, size), companyQueryWrapper);
        if (tdCompanyInfoPage.getRecords().isEmpty()) {
            return null;
        }
        Page<CompanyInfoVO> companyInfoVOPage = new Page<>(tdCompanyInfoPage.getCurrent(), tdCompanyInfoPage.getSize(), tdCompanyInfoPage.getTotal());
        List<Long> userIds = tdCompanyInfoPage.getRecords().stream().map(TDCompanyInfo::getUserId).distinct().toList();
        List<TDUser> users = userMapper.selectByIds(userIds);

        List<CompanyInfoVO> companyInfoVOList = tdCompanyInfoPage.getRecords().stream().map(tdCompanyInfo -> {
            CompanyInfoVO companyInfoVO = new CompanyInfoVO();
            BeanUtils.copyProperties(tdCompanyInfo, companyInfoVO);
            users.stream().filter(u -> u.getUserId().equals(tdCompanyInfo.getUserId())).findFirst()
                    .ifPresent(user -> companyInfoVO.setUserName(user.getUserName()));
            companyInfoVO.setCompanyLogo("http://" + ip + ":" + port + tdCompanyInfo.getCompanyLogo());
            return companyInfoVO;
        }).collect(Collectors.toList());

        companyInfoVOPage.setRecords(companyInfoVOList);
        return companyInfoVOPage;
    }

    @Override
    public Object deleteCompany(Long companyId) {
        tdCompanyInfoMapper.deleteById(companyId);
        return null;
    }
}
