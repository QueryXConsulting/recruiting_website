package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDJob;
import com.queryx.recruiting_website.domain.TDJobResume;
import com.queryx.recruiting_website.domain.TDResume;
import com.queryx.recruiting_website.domain.TDResumeAttachments;
import com.queryx.recruiting_website.domain.dto.SelectResumeDto;
import com.queryx.recruiting_website.mapper.TDJobMapper;
import com.queryx.recruiting_website.mapper.TDJobResumeMapper;
import com.queryx.recruiting_website.mapper.TDResumeAttachmentsMapper;
import com.queryx.recruiting_website.mapper.TDResumeMapper;
import com.queryx.recruiting_website.service.TDResumeService;
import com.queryx.recruiting_website.domain.vo.ResumeListVo;
import com.queryx.recruiting_website.domain.vo.ResumeVo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("tDResumeService")
@RequiredArgsConstructor
public class TDResumeServiceImpl extends ServiceImpl<TDResumeMapper, TDResume> implements TDResumeService {

    @Resource
    private TDJobMapper tdJobMapper;
    @Resource
    private TDJobResumeMapper tdJobResumeMapper;
    @Resource
    private TDResumeMapper tdResumeMapper;
    @Resource
    private TDResumeAttachmentsMapper tdResumeAttachmentsMapper;

    @Override
    public List<ResumeListVo> insertJobInfo(Long companyId) {
        LambdaQueryWrapper<TDJob> jobQueryWrapper = new LambdaQueryWrapper<>();
        jobQueryWrapper.eq(TDJob::getCompanyId, companyId)
                .eq(TDJob::getJobStatus, Common.STATUS_PUBLISH.getCode())
                .eq(TDJob::getJobReview, Common.REVIEW_SUCCESS.getCode());
        List<TDJob> tdJobs = tdJobMapper.selectList(jobQueryWrapper);
        if (tdJobs.isEmpty()) {
            return null;
        }

        List<Long> jobIdLst = tdJobs.stream().map(TDJob::getJobId).toList();
        LambdaQueryWrapper<TDJobResume> jobResumeLambdaQueryWrapper = Wrappers.<TDJobResume>lambdaQuery()
                .in(TDJobResume::getJobId, jobIdLst);
        List<TDJobResume> tdJobResumes = tdJobResumeMapper.selectList(jobResumeLambdaQueryWrapper);
        if (tdJobResumes.isEmpty()) {
            return null;
        }

        return tdJobResumes.stream()
                .flatMap(tdJobResume -> tdJobs.stream()
                        .filter(tdJob -> tdJobResume.getJobId().equals(tdJob.getJobId()))
                        .map(tdJob -> {
                            ResumeListVo resumeListVo = new ResumeListVo();
                            resumeListVo.setResumeId(tdJobResume.getResumeId());
                            resumeListVo.setResumeName(tdJobResume.getResumeName());
                            resumeListVo.setResumeType(tdJobResume.getResumeType());
                            resumeListVo.setJobPosition(tdJob.getJobPosition());
                            return resumeListVo;
                        })).toList();
    }

    @Override
    public ResumeVo selectResume(SelectResumeDto selectResumeDto) {
        ResumeVo resumeVo = new ResumeVo();
        if (selectResumeDto.getResumeType().equals(Common.RESUME_ONLINE.getCode())) {
            LambdaQueryWrapper<TDResume> QueryWrapper = new LambdaQueryWrapper<>();
            QueryWrapper.eq(TDResume::getResumeId,selectResumeDto.getResumeId())
                    .eq(TDResume::getResumeStatus,"0")
                    .eq(TDResume::getResumeReview,Common.REVIEW_SUCCESS.getCode());
            TDResume tdResume = tdResumeMapper.selectOne(QueryWrapper);
            BeanUtils.copyProperties(tdResume, resumeVo);
            return resumeVo;
        }

        LambdaQueryWrapper<TDResumeAttachments> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TDResumeAttachments::getResumeAttachmentId,selectResumeDto.getResumeId())
                .eq(TDResumeAttachments::getIsDeleted,"0")
                .eq(TDResumeAttachments::getAttachmentsReview,Common.REVIEW_SUCCESS.getCode());

        TDResumeAttachments resume = tdResumeAttachmentsMapper.selectOne(wrapper);
        BeanUtils.copyProperties(resume,resumeVo);
        return resumeVo;
    }
}
