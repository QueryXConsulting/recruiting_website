package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDCompanyInfo;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.dto.RegisterCompanyDto;
import com.queryx.recruiting_website.domain.vo.CompanyInfoDto;
import com.queryx.recruiting_website.domain.vo.CompanyInfoVO;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.TDCompanyInfoMapper;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import com.queryx.recruiting_website.service.TDCompanyInfoService;
import com.queryx.recruiting_website.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TDCompanyInfoServiceImpl extends ServiceImpl<TDCompanyInfoMapper, TDCompanyInfo> implements TDCompanyInfoService {
    @Resource
    private TDCompanyInfoMapper tdCompanyInfoMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private TDUserMapper userMapper;


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
        // 校验用户名是否有相同的
        if (StringUtils.hasText(companyInfoUsername)) {
            LambdaQueryWrapper<TDCompanyInfo> QueryWrapper = new LambdaQueryWrapper<>();
            QueryWrapper.eq(TDCompanyInfo::getCompanyInfoUsername, companyInfoUsername);
            if (count(QueryWrapper) > 0) {
                throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
            }
        }

        TDCompanyInfo tdCompanyInfo = new TDCompanyInfo();
        BeanUtils.copyProperties(companyInfoDto, tdCompanyInfo);
        // 未修改密码前端会传一个空字符串进来
        if (StringUtils.hasText(companyInfoDto.getCompanyInfoPassword())) {
            tdCompanyInfo.setCompanyInfoPassword(passwordEncoder.encode(tdCompanyInfo.getCompanyInfoPassword()));
        } else {
            tdCompanyInfo.setCompanyInfoPassword(null);
        }

        LambdaUpdateWrapper<TDCompanyInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TDCompanyInfo::getCompanyInfoId, companyInfoDto.getCompanyInfoId());
        tdCompanyInfo.setCompanyInfoStatus(companyInfoDto.getCompanyInfoStatus());
        tdCompanyInfo.setCompanyInfoReview(companyInfoDto.getCompanyInfoReview());
        if (companyInfoDto.getEnterpriseReview() != null) {
            tdCompanyInfo.setEnterpriseReview(companyInfoDto.getEnterpriseReview());
        }
        try {
            // 资质上传
            handlePdfFiles(pdfFiles, tdCompanyInfo, companyInfoDto.getCompanyInfoId());
            handleApplyFile(applyFiles, tdCompanyInfo, companyInfoDto.getCompanyInfoId());

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }


        update(tdCompanyInfo, updateWrapper);

        return null;


    }


    private void handlePdfFiles(List<MultipartFile> pdfFiles, TDCompanyInfo tdCompanyInfo, Long companyInfoId) throws IOException {
        if (pdfFiles == null) return;

        StringBuilder enterpriseFiles = new StringBuilder();
        long currentTimeMillis = System.currentTimeMillis();

        for (MultipartFile pdf : pdfFiles) {
            String fileName = pdf.getOriginalFilename();
            validateAndSaveFile(pdf, Common.enterpriseFilePath, currentTimeMillis, fileName);
            appendFilePathToBuilder(enterpriseFiles, currentTimeMillis, fileName);
        }
        // 删除以前的
        deleteOldEnterpriseFiles(companyInfoId);
        tdCompanyInfo.setEnterpriseFile(enterpriseFiles.toString());
    }

    private void handleApplyFile(MultipartFile applyFiles, TDCompanyInfo tdCompanyInfo, Long companyInfoId) throws IOException {
        if (applyFiles == null) return;

        String fileName = applyFiles.getOriginalFilename();
        long currentTimeMillis = System.currentTimeMillis();

        validateAndSaveFile(applyFiles, Common.uploadPath, currentTimeMillis, fileName);
        tdCompanyInfo.setCompanyLogo("/avatar_files/" + currentTimeMillis + "_" + fileName);
        // 删除以前的图片
        deleteOldCompanyLogo(companyInfoId);
    }

    private void validateAndSaveFile(MultipartFile file, String path, long timestamp, String fileName) throws IOException {
        if (!SecurityUtils.isAllowedFileType(SecurityUtils.getFileExtension(fileName))) {
            throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
        }

        File uploadDir = new File(path);
        File destFile = new File(uploadDir, timestamp + "_" + fileName);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        file.transferTo(destFile);
    }

    private void appendFilePathToBuilder(StringBuilder builder, long timestamp, String fileName) {
        if (builder.length() > 0) {
            builder.append(",");
        }
        builder.append("/enterprise_files/").append(timestamp).append("_").append(fileName);
    }

    private void deleteOldEnterpriseFiles(Long companyInfoId) {
        String oldFiles = getById(companyInfoId).getEnterpriseFile();
        if (StringUtils.hasText(oldFiles)) {
            Arrays.stream(oldFiles.split(","))
                    .filter(fileUrl -> fileUrl != null && !fileUrl.trim().isEmpty())
                    .map(fileUrl -> new File(Common.enterpriseFilePath + fileUrl.substring(fileUrl.lastIndexOf('/') + 1)))
                    .filter(File::exists)
                    .forEach(File::delete);
        }
    }

    private void deleteOldCompanyLogo(Long companyInfoId) {
        String oldLogo = getById(companyInfoId).getCompanyLogo();
        if (StringUtils.hasText(oldLogo)) {
            File oldFile = new File(Common.uploadPath + oldLogo.substring(oldLogo.lastIndexOf('/') + 1));
            if (oldFile.exists()) {
                oldFile.delete();
            }
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
        // 数据类型转化
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
            Path filePath = Paths.get(Common.enterpriseFilePath, fileName);
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

    @Override
    public Object registerCompany(RegisterCompanyDto registerCompanyDto) {

        if (count(new LambdaQueryWrapper<TDCompanyInfo>()
                .eq(TDCompanyInfo::getCompanyInfoUsername, registerCompanyDto.getCompanyInfoUsername())) > 0) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }

        boolean emailExists = userMapper.exists(new LambdaQueryWrapper<TDUser>()
                .eq(TDUser::getUserEmail, registerCompanyDto.getUserEmail()));
        boolean phoneExists = userMapper.exists(new LambdaQueryWrapper<TDUser>()
                .eq(TDUser::getUserPhone, registerCompanyDto.getUserPhone()));
        if (emailExists) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
        if (phoneExists) {
            throw new SystemException(AppHttpCodeEnum.PHONE_EXIST);
        }

        TDCompanyInfo tdCompanyInfo = new TDCompanyInfo();
        BeanUtils.copyProperties(registerCompanyDto, tdCompanyInfo);
        tdCompanyInfo.setCompanyRegisterTime(new Date());
        tdCompanyInfo.setCompanyInfoStatus("0");
        tdCompanyInfo.setCompanyInfoPassword(passwordEncoder.encode(registerCompanyDto.getCompanyInfoPassword()));
        save(tdCompanyInfo);

        TDUser tdUser = new TDUser();
        tdUser.setCompanyInfoId(tdCompanyInfo.getCompanyInfoId());
        BeanUtils.copyProperties(registerCompanyDto, tdUser);
        // 设置为公司角色boss
        tdUser.setUserRole("4");
        tdUser.setUserRegisterTime(new Date());
        // 设置为首次登录
        tdUser.setIsFirstLogin("1");
        tdUser.setUserPassword(passwordEncoder.encode(registerCompanyDto.getCompanyInfoPassword()));
        userMapper.insert(tdUser);
        return null;
    }
}
