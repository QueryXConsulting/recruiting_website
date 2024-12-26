package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDJob;
import com.queryx.recruiting_website.domain.TDJobResume;
import com.queryx.recruiting_website.domain.TDResume;
import com.queryx.recruiting_website.mapper.TDJobMapper;
import com.queryx.recruiting_website.mapper.TDJobResumeMapper;
import com.queryx.recruiting_website.mapper.TDResumeMapper;
import com.queryx.recruiting_website.service.TDResumeService;
import com.queryx.recruiting_website.vo.ResumeListVo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service("tDResumeService")
@RequiredArgsConstructor
public class TDResumeServiceImpl extends ServiceImpl<TDResumeMapper, TDResume> implements TDResumeService {

    @Resource
    private TDJobMapper tdJobMapper;
    @Resource
    private TDJobResumeMapper tdJobResumeMapper;

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
        if (tdJobResumes.isEmpty()){
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
}
