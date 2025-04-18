package com.queryx.recruiting_website.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.*;
import com.queryx.recruiting_website.domain.dto.SearchDTO;
import com.queryx.recruiting_website.domain.vo.*;
import com.queryx.recruiting_website.domain.vo.search.SearchCompanyVO;
import com.queryx.recruiting_website.domain.vo.search.SearchJobVO;
import com.queryx.recruiting_website.mapper.*;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.IKAnalyzerUtil;
import com.queryx.recruiting_website.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class QueryServiceImpl implements QueryService {

    @Autowired
    private TDUserMapper userMapper;

    @Autowired
    private TDResumeMapper resumeMapper;

    @Autowired
    private TDResumeAttachmentsMapper attachmentsMapper;

    @Autowired
    private TDJobMapper jobInfoMapper;

    @Autowired
    private TDJobNatureMapper jobNatureMapper;

    @Autowired
    private TDJobResumeMapper jobResumeMapper;

    @Autowired
    private TDCompanyInfoMapper companyInfoMapper;

    @Override
    public ResumeVO getOnlineResume(Long id) {
        // 构建SQL语句，查询简历信息
        final ResumeVO resumeVO = new ResumeVO();
        final TDResume tdResume = resumeMapper.selectOne(new LambdaQueryWrapper<TDResume>()
                .eq(TDResume::getResumeId, id)
                .eq(TDResume::getResumeStatus, Common.STATUS_ENABLE)
                .eq(TDResume::getResumeReview, Common.REVIEW_OK));
        if (tdResume == null) {
            return null;
        }
        // 封装简历返回信息
        BeanUtils.copyProperties(tdResume, resumeVO);

        if (resumeVO.getResumeId() == null) {
            return null;
        }
        return resumeVO;
    }

    @Override
    public List<AttachmentsResumeVO> getResumeAttachmentList(Long id) {
        final List<AttachmentsResumeVO> list = new ArrayList<>();
        // 构建SQL语句，查询附件简历信息
        attachmentsMapper.selectList(
                new LambdaQueryWrapper<TDResumeAttachments>().eq(TDResumeAttachments::getUserId, id)
                        .eq(TDResumeAttachments::getAttachmentsReview, Common.REVIEW_OK)
                        .eq(TDResumeAttachments::getIsDeleted, Common.NOT_DELETE),
                // 消费查询结果集，并封装简历附件信息（该方法一个入参，无返回值）
                resultContext -> {
                    if (resultContext == null) {
                        return;
                    }
                    final AttachmentsResumeVO attachmentsResumeListVO = new AttachmentsResumeVO();
                    final TDResumeAttachments attachments = resultContext.getResultObject();
                    BeanUtils.copyProperties(attachments, attachmentsResumeListVO);
                    String filePath = attachments.getFilePath().replaceAll("\\\\", "/");
                    attachmentsResumeListVO.setFilePath(Common.getBaseURL() + filePath);
                    list.add(attachmentsResumeListVO);
                }
        );
        return list.isEmpty() ? null : list;
    }

    @Override
    public CommonResp<Page<?>> getSearchList(SearchDTO searchDTO) {
        Page<?> result = null;
        switch (searchDTO.getSearchType()) {
            case JOB -> result = getJobList(
                    searchDTO.getKeyword().trim(), searchDTO.getPage(), searchDTO.getSize(),
                    searchDTO.getIsAsc(), searchDTO.getEducation(), searchDTO.getNature()
            );
            case COMPANY ->
                    result = getCompanyList(searchDTO.getKeyword().trim(), searchDTO.getPage(), searchDTO.getSize(), searchDTO.getIsAsc()).getContent();
            default -> {
                return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
            }
        }
        return CommonResp.success(result);
    }

    @Override
    public JobVO getJob(Long id) {
        final TDJob tdJob = jobInfoMapper.selectById(id);
        if (tdJob == null) {
            return null;
        }
        if (Common.JOB_STATUS_ENABLE_NOT_OK.equals(tdJob.getJobStatus()) &&
                Common.REVIEW_NOT_OK.equals(tdJob.getJobReview())) {
            return null;
        }
        final JobVO jobVO = new JobVO();
        BeanUtils.copyProperties(tdJob, jobVO);
        // 查询改岗位是否已投递过简历
        LambdaQueryWrapper<TDJobResume> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(TDJobResume::getJobResumeId);
        queryWrapper.eq(TDJobResume::getJobId, id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 判断用户是否登录
        if (authentication != null) {
            queryWrapper.eq(TDJobResume::getUserId, SecurityUtils.getLoginUser().getTdUser().getUserId());
        }
        jobInfoMapper.update(new LambdaUpdateWrapper<TDJob>().eq(TDJob::getJobId, id)
                .set(TDJob::getJobView, tdJob.getJobView() + 1));
        jobVO.setJobIsDelivery(jobResumeMapper.selectCount(queryWrapper) > 0);
        // 返回数据
        return jobVO;
    }

    @Override
    @Cacheable(value = "jobList", key = "#keyword + #page + #pageSize + #isAsc + #education + #nature")
    public Page<SearchJobVO> getJobList(String keyword, Integer page, Integer pageSize, boolean isAsc, String education, String nature) {
        // 构建SQL语句，查询招聘信息
        LambdaQueryWrapper<TDJob> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TDJob::getJobReview, Common.REVIEW_OK);
        queryWrapper.eq(TDJob::getDelFlag, Common.NOT_DELETE);
        queryWrapper.eq(TDJob::getJobStatus, Common.JOB_STATUS_ENABLE_OK);

        queryWrapper.nested(i -> {
            boolean firstCondition = true; // 用于标记是否是第一个条件，以避免在第一个条件前添加多余的"OR"
            for (String s : IKAnalyzerUtil.cut(keyword)) {
                if (firstCondition) {
                    i.like(TDJob::getJobPosition, s);
                    firstCondition = false;
                } else {
                    i.or().like(TDJob::getJobPosition, s);
                }
            }
        });

        queryWrapper.eq(nature != null && !nature.isEmpty(), TDJob::getJobNature, nature);
        queryWrapper.eq(education != null && !education.isEmpty(), TDJob::getJobEducation, education);
        queryWrapper.orderBy(true, isAsc, TDJob::getJobTime);
        // 构建分页对象
        Page<TDJob> jobPage = jobInfoMapper.selectPage(new Page<>(page, pageSize), queryWrapper);
        if (jobPage == null) return null;
        Page<SearchJobVO> resPage = new Page<>(jobPage.getCurrent(), jobPage.getSize(), jobPage.getTotal());
        List<SearchJobVO> list = new ArrayList<>();
        for (TDJob record : jobPage.getRecords()) {
            String companyName = companyInfoMapper.selectById(record.getCompanyId()).getCompanyInfoName();
            SearchJobVO searchJobVO = new SearchJobVO();
            BeanUtils.copyProperties(record, searchJobVO);
            searchJobVO.setCompanyName(companyName);
            list.add(searchJobVO);
        }
        return resPage.setRecords(list);
    }


    @Override
    @Cacheable(value = "companyList", key = "#keyword + #page + #pageSize + #isAsc")
    public CommonResp<Page<SearchCompanyVO>> getCompanyList(String keyword, Integer page, Integer pageSize, boolean isAsc) {
        // 构建SQL语句，查询招聘信息
        LambdaQueryWrapper<TDCompanyInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TDCompanyInfo::getCompanyInfoReview, Common.REVIEW_OK);
        queryWrapper.eq(TDCompanyInfo::getEnterpriseReview, Common.REVIEW_OK);
        queryWrapper.eq(TDCompanyInfo::getCompanyInfoStatus, Common.STATUS_ENABLE);

        queryWrapper.nested(i -> {
            boolean firstCondition = true;
            for (String s : IKAnalyzerUtil.cut(keyword)) {
                if (firstCondition) {
                    i.like(TDCompanyInfo::getCompanyInfoName, s);
                    firstCondition = false;
                } else {
                    i.or().like(TDCompanyInfo::getCompanyInfoName, s);
                }
            }
        });

        queryWrapper.orderBy(true, isAsc, TDCompanyInfo::getCompanyRegisterTime);
        // 构建分页对象
        Page<TDCompanyInfo> company = companyInfoMapper.selectPage(new Page<>(page, pageSize), queryWrapper);
        if (company == null) {
            return CommonResp.success(null);
        }
        Page<SearchCompanyVO> resPage = new Page<>(company.getCurrent(), company.getSize(), company.getTotal());
        List<SearchCompanyVO> list = new ArrayList<>();
        for (TDCompanyInfo record : company.getRecords()) {
            SearchCompanyVO searchJobVO = new SearchCompanyVO();
            BeanUtils.copyProperties(record, searchJobVO);
            list.add(searchJobVO);
        }
        return CommonResp.success(resPage.setRecords(list));
    }

    @Override
    public CommonResp<AllResumeVO> getAllResume(Long userId) {
        // 容器准备
        AllResumeVO allResumeVO = new AllResumeVO();
        ArrayList<AllResumeVO.AttachmentsResume> attachmentsResumeList = new ArrayList<>();
        allResumeVO.setAttachmentsResumes(attachmentsResumeList);
        // 根据用户id查询用户信息
        TDUser user = userMapper.selectById(userId);
        if (user == null) return CommonResp.fail(AppHttpCodeEnum.USER_NOT_EXIST, null);
        if (Common.STATUS_DISABLE.equals(user.getUserStatus()) || Common.DELETE.equals(user.getDelFlag())) {
            return CommonResp.fail(AppHttpCodeEnum.USER_NOT_EXIST, null);
        }
        allResumeVO.setUserRegisterTime(user.getUserRegisterTime());

        // 查询在线简历信息
        LambdaQueryWrapper<TDResume> resumeWrapper = new LambdaQueryWrapper<>();
        resumeWrapper.eq(TDResume::getResumeId, user.getResumeId());
        resumeWrapper.eq(TDResume::getResumeReview, Common.REVIEW_OK);
        resumeWrapper.eq(TDResume::getResumeStatus, Common.STATUS_ENABLE);
        TDResume resumeResult = resumeMapper.selectOne(resumeWrapper);
        // 用户一定存在在线简历
        if (resumeResult == null) return CommonResp.fail(AppHttpCodeEnum.RESUME_NOT_EXIST, null);
        allResumeVO.setResumeId(resumeResult.getResumeId());

        // 查询附件简历信息
        LambdaQueryWrapper<TDResumeAttachments> attachmentsWrapper = new LambdaQueryWrapper<>();
        attachmentsWrapper.eq(TDResumeAttachments::getUserId, user.getUserId());
        attachmentsWrapper.eq(TDResumeAttachments::getAttachmentsReview, Common.REVIEW_OK);
        attachmentsWrapper.eq(TDResumeAttachments::getIsDeleted, Common.NOT_DELETE);
        List<TDResumeAttachments> attachmentsResultList = attachmentsMapper.selectList(attachmentsWrapper);
        // 用户可能没有附件简历
        if (attachmentsResultList == null || attachmentsResultList.isEmpty()) {
            return CommonResp.success(allResumeVO);
        }
        for (TDResumeAttachments data : attachmentsResultList) {
            AllResumeVO.AttachmentsResume attachmentsResumeVO1 = new AllResumeVO.AttachmentsResume();
            BeanUtils.copyProperties(data, attachmentsResumeVO1);
            allResumeVO.getAttachmentsResumes().add(attachmentsResumeVO1);
        }
        return CommonResp.success(allResumeVO);
    }


    @Override
    @Cacheable(value = "jobNatureList", key = "#root.methodName")
    public CommonResp<List<JobNatureVO>> getJobNatureList() {
        // 构建SQL语句，查询职位性质信息
        final LambdaQueryWrapper<TDJobNature> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TDJobNature::getNatureStatus, Common.STATUS_ENABLE);
        final List<TDJobNature> natures = jobNatureMapper.selectList(queryWrapper);
        if (natures == null || natures.isEmpty()) {
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        // 封装职位性质信息
        ArrayList<JobNatureVO> jobNatureVOS = new ArrayList<>();
        for (TDJobNature nature : natures) {
            JobNatureVO jobNatureVO = new JobNatureVO();
            jobNatureVO.setNatureId(nature.getNatureId());
            jobNatureVO.setNatureName(nature.getJobNatureName());
            jobNatureVOS.add(jobNatureVO);
        }
        return CommonResp.success(jobNatureVOS);
    }


}
