package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDCompanyInfo;
import com.queryx.recruiting_website.domain.vo.CompanyInfoDto;
import com.queryx.recruiting_website.domain.vo.CompanyInfoVO;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDCompanyInfoMapper;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import com.queryx.recruiting_website.service.TDCompanyInfoService;
import com.queryx.recruiting_website.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Value("${file.upload-path-enterpriseFile}")
    private String enterpriseFilePath;


    @Override
    public CompanyInfoDto selectCompanyInfo(Long companyId) {
        LambdaQueryWrapper<TDCompanyInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TDCompanyInfo::getCompanyInfoId, companyId)
                .eq(TDCompanyInfo::getCompanyInfoStatus, Common.STATUS_ENABLE);
        TDCompanyInfo tdCompanyInfo = tdCompanyInfoMapper.selectOne(lambdaQueryWrapper);
        CompanyInfoDto companyInfoDto = new CompanyInfoDto();
        BeanUtils.copyProperties(tdCompanyInfo, companyInfoDto);
        companyInfoDto.setCompanyLogo(Common.getImgURL() + companyInfoDto.getCompanyLogo());
        return companyInfoDto;
    }

    @Override
    public CompanyInfoDto updateCompanyInfo(CompanyInfoDto companyInfoDto, MultipartFile applyFiles, List<MultipartFile> pdfFiles) {
        String companyInfoUsername = companyInfoDto.getCompanyInfoUsername();
        if (StringUtils.hasText(companyInfoUsername)) {
            LambdaQueryWrapper<TDCompanyInfo> QueryWrapper = new LambdaQueryWrapper<>();
            QueryWrapper.eq(TDCompanyInfo::getCompanyInfoUsername, companyInfoUsername);
            if (count(QueryWrapper) > 0) {
                throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
            }
        }

        StringBuilder enterpriseFiles = new StringBuilder();
        TDCompanyInfo tdCompanyInfo = new TDCompanyInfo();
        BeanUtils.copyProperties(companyInfoDto, tdCompanyInfo);
        if (StringUtils.hasText(companyInfoDto.getCompanyInfoPassword())) {
            tdCompanyInfo.setCompanyInfoPassword(passwordEncoder.encode(tdCompanyInfo.getCompanyInfoPassword()));
        }

        UpdateWrapper<TDCompanyInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("company_info_id", companyInfoDto.getCompanyInfoId());
        tdCompanyInfo.setCompanyInfoStatus(companyInfoDto.getCompanyInfoStatus());
        tdCompanyInfo.setCompanyInfoReview(companyInfoDto.getCompanyInfoReview());
        if (companyInfoDto.getEnterpriseReview() != null) {
            tdCompanyInfo.setEnterpriseReview(companyInfoDto.getEnterpriseReview());
        }
        try {
            if (pdfFiles != null) {
                long currentTimeMillis = System.currentTimeMillis();
                for (MultipartFile pdf : pdfFiles) {
                    String fileName = pdf.getOriginalFilename();
                    if (!SecurityUtils.isAllowedFileType(SecurityUtils.getFileExtension(fileName))) {
                        throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
                    }
                    File uploadDir = new File(enterpriseFilePath);
                    File destFile = new File(uploadDir, currentTimeMillis + "_" + fileName);
                    if (!destFile.getParentFile().exists()) {
                        destFile.getParentFile().mkdirs();
                    }
                    pdf.transferTo(destFile);
                    if (enterpriseFiles.length() > 0) {
                        enterpriseFiles.append(",");
                    }
                    enterpriseFiles.append("/enterprise_files/").append(currentTimeMillis).append("_").append(fileName);
                }
                // 删除旧的图片
                String oldCompanyLogoURL = getById(companyInfoDto.getCompanyInfoId()).getEnterpriseFile();
                String[] fileUrls = oldCompanyLogoURL.split(",");
                for (String fileUrl : fileUrls) {
                    if (fileUrl != null && !fileUrl.trim().isEmpty()) {
                        int lastIndex = fileUrl.lastIndexOf('/');
                        String fileName = fileUrl.substring(lastIndex + 1);
                        File oldFile = new File(enterpriseFilePath + fileName);
                        if (oldFile.exists()) {
                            oldFile.delete();
                        }
                    }
                }
                tdCompanyInfo.setEnterpriseFile(String.valueOf(enterpriseFiles));
            }

            if (applyFiles != null) {
                String fileName = applyFiles.getOriginalFilename();
                long currentTimeMillis = System.currentTimeMillis();
                if (!SecurityUtils.isAllowedFileType(SecurityUtils.getFileExtension(fileName))) {
                    throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
                }
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
            }

           update(tdCompanyInfo, updateWrapper);

            return null;
        } catch (IOException e) {
            e.printStackTrace();
            throw new SystemException(AppHttpCodeEnum.FILE_UPLOAD_ERROR);
        }

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
        List<CompanyInfoVO> companyInfoVOList = tdCompanyInfoPage.getRecords().stream().map(tdCompanyInfo -> {
            CompanyInfoVO companyInfoVO = new CompanyInfoVO();
            BeanUtils.copyProperties(tdCompanyInfo, companyInfoVO);
//            String enterpriseFile = tdCompanyInfo.getEnterpriseFile();
//            if (StringUtils.hasText(enterpriseFile)) {
//                List<String> urls = Arrays.stream(enterpriseFile.split(","))
//                        .map(url -> "http://" + ip + ":" + port + url)
//                        .collect(Collectors.toList());
//                companyInfoVO.setEnterpriseFiles(urls);
//            }
            companyInfoVO.setCompanyLogo(Common.getImgURL() + tdCompanyInfo.getCompanyLogo());
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

    @Override
    public Map<String, byte[]> getEnterpriseFiles(Long companyId) {
        TDCompanyInfo companyInfo = getById(companyId);
        List<String> enterpriseFiles = Arrays.stream(companyInfo.getEnterpriseFile().split(",")).toList();
        if (enterpriseFiles.isEmpty()) {
            return null;
        }
        Map<String, byte[]> map = new HashMap<>();
        enterpriseFiles.forEach(enterpriseFile -> {
            int lastIndex = enterpriseFile.lastIndexOf('/');
            String fileName = enterpriseFile.substring(lastIndex + 1);
            Path filePath = Paths.get(enterpriseFilePath, fileName);
            File file = filePath.toFile();

            if (!file.exists()) {
                throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
            }
            try {
                byte[] fileContent = Files.readAllBytes(filePath);
                map.put(fileName, fileContent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return map;
    }
}
