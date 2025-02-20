package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.queryx.recruiting_website.domain.TDInterview;
import com.queryx.recruiting_website.domain.TDJob;
import com.queryx.recruiting_website.domain.vo.*;
import com.queryx.recruiting_website.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDResume;
import com.queryx.recruiting_website.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import com.queryx.recruiting_website.domain.TDResumeAttachments;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
//@Transactional(rollbackFor = Exception.class)
public class QueryServiceImpl implements QueryService {

    @Autowired
    private TDResumeMapper resumeMapper;

    @Autowired
    private TDResumeAttachmentsMapper attachmentsMapper;

    @Autowired
    private InterviewMapper interviewMapper;

    @Autowired
    private TDJobMapper jobInfoMapper;

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
        // 封装简历返回信息
        BeanUtils.copyProperties(tdResume, resumeVO);
        if (resumeVO.getResumeId() == null) {
            return null;
        }
        return resumeVO;
    }

    @Override
    public List<AttachmentsResumeVO> getResumeAttachmentList(Long id) {
        final AttachmentsResumeVO attachmentsResumeListVO = new AttachmentsResumeVO();
        final List<AttachmentsResumeVO> list = new ArrayList<>();
        // 构建SQL语句，查询附件简历信息
        attachmentsMapper.selectList(
                new LambdaQueryWrapper<TDResumeAttachments>().eq(TDResumeAttachments::getUserId, id)
                        // TODO 查询附件简历列表：目前审核状态为未通过，且未删除
                        .eq(TDResumeAttachments::getAttachmentsReview, Common.REVIEW_NOT_OK)
                        .eq(TDResumeAttachments::getIsDeleted, Common.NOT_DELETE),
                // 消费查询结果集，并封装简历附件信息（该方法一个入参，无返回值）
                resultContext -> {
                    if (resultContext == null) {
                        return;
                    }
                    final TDResumeAttachments attachments = resultContext.getResultObject();
                    BeanUtils.copyProperties(attachments, attachmentsResumeListVO);
                    list.add(attachmentsResumeListVO);
                });
        return list.isEmpty() ? null : list;
    }

    @Override
    public InterviewVO getInterview(Long id) {
        // 构建SQL语句，查询简历信息
        final InterviewVO interviewVO = new InterviewVO();
        List<TDInterview> list = interviewMapper.getInterviewsByUserId(id);
        if (list == null || list.isEmpty()) {
            return null;
        }
        list.forEach(data -> BeanUtils.copyProperties(data, interviewVO));
        return interviewVO;
    }

    @Override
    public JobVO getJob(Long id) {
        final TDJob tdJob = jobInfoMapper.selectById(id);
        if (!isJobExist(tdJob)) {
            return null;
        }
        final JobVO jobVO = new JobVO();
        BeanUtils.copyProperties(tdJob, jobVO);
        return jobVO;
    }

    @Override // TODO 未测试
    public List<JobCompanyListVO> getJobList(String keyword, Integer page, Integer pageSize) {
        // 构建SQL语句，查询招聘信息
        LambdaQueryWrapper<TDJob> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(TDJob::getJobPosition, keyword);
        queryWrapper.eq(TDJob::getJobReview, Common.REVIEW_OK);
        queryWrapper.eq(TDJob::getDelFlag, Common.NOT_DELETE);
        queryWrapper.eq(TDJob::getJobStatus, Common.JOB_STATUS_ENABLE_OK);
        // 构建分页对象
        Page<TDJob> jobPage = new Page<>(page, pageSize);
        List<TDJob> records = jobInfoMapper.selectPage(jobPage, queryWrapper).getRecords();
        if (records == null || records.isEmpty()) return null;
        List<JobCompanyListVO> list = new ArrayList<>();
        for (TDJob job : records) {
            String companyName = companyInfoMapper.selectById(job.getCompanyId()).getCompanyInfoName();
            JobCompanyListVO jobCompanyListVO = new JobCompanyListVO();
            BeanUtils.copyProperties(job, jobCompanyListVO);
            jobCompanyListVO.setCompanyName(companyName);
            list.add(jobCompanyListVO);
        }
        return list;
    }


    // 招聘信息存在返回true，不存在返回false
    private boolean isJobExist(TDJob job) {
        if (job == null) {
            return false;
        }
        return job.getJobStatus().equals(Common.JOB_STATUS_ENABLE_OK) &&
                job.getJobReview().equals(Common.REVIEW_OK);
    }
}
