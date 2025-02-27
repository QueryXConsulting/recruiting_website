package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.*;
import com.queryx.recruiting_website.domain.dto.SendInterviewDto;
import com.queryx.recruiting_website.domain.dto.UpdateInterviewDto;
import com.queryx.recruiting_website.domain.vo.InterviewListVo;
import com.queryx.recruiting_website.mapper.*;
import com.queryx.recruiting_website.service.InterviewService;
import com.queryx.recruiting_website.service.TDResumeService;
import com.queryx.recruiting_website.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InterviewServiceImpl extends ServiceImpl<InterviewMapper, TDInterview> implements InterviewService {


    @Resource
    private TDJobMapper jobMapper;
    @Resource
    private TDResumeService tdResumeService;
    @Resource
    private TDJobResumeMapper tdJobResumeMapper;

    @Override
    public Object sendInterviewDto(SendInterviewDto sendInterviewDto) {
        TDInterview tdInterview = new TDInterview();
        BeanUtils.copyProperties(sendInterviewDto, tdInterview);
        tdInterview.setUserId(sendInterviewDto.getUserId());
        tdInterview.setCompanyId(SecurityUtils.getLoginUser().getTdUser().getCompanyInfoId());
        tdInterview.setInterviewStatus("0");
        tdInterview.setJobId(sendInterviewDto.getJobId());
        save(tdInterview);
        return null;
    }

    @Override
    public Object selectInterviewList(Integer page, Integer size, Long jobId) {

        LambdaQueryWrapper<TDInterview> tdInterviewLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tdInterviewLambdaQueryWrapper.eq(TDInterview::getCompanyId, SecurityUtils.getLoginUser().getTdUser().getCompanyInfoId())
                .eq(TDInterview::getJobId,jobId)
                .eq(TDInterview::getIsDeleted, Common.NOT_DELETE);
        Page<TDInterview> interviewPage = page(new Page<>(page, size), tdInterviewLambdaQueryWrapper);


        List<TDJobResume> tdJobResumes = tdJobResumeMapper.selectList(new LambdaQueryWrapper<TDJobResume>().eq(TDJobResume::getJobId,jobId));
        Map<Long, String> resumeMap = tdJobResumes.stream()
                .collect(Collectors.toMap(TDJobResume::getUserId, TDJobResume::getResumeName));
        Map<Long, Long> resumeIdmap = tdJobResumes.stream().
                collect(Collectors.toMap(TDJobResume::getUserId, TDJobResume::getResumeId));

        Page<InterviewListVo> interviewListVoPage = new Page<>(interviewPage.getCurrent(), interviewPage.getSize(), interviewPage.getTotal());
        interviewListVoPage.setRecords(interviewPage.getRecords().stream().map(interview -> {
            InterviewListVo interviewListVo = new InterviewListVo();
            BeanUtils.copyProperties(interview, interviewListVo);
            String resumeName = resumeMap.get(interview.getUserId());
            interviewListVo.setInterviewId(resumeIdmap.get(interview.getUserId()));
            if (resumeName != null) {
                interviewListVo.setResumeName(resumeName);
            }
            return interviewListVo;
        }).toList());

        return interviewListVoPage;

    }

    @Override
    public Object updateInterview(UpdateInterviewDto updateInterviewDto) {
        TDInterview tdInterview = new TDInterview();
        BeanUtils.copyProperties(updateInterviewDto, tdInterview);
        // TODO 待修改
//        if (tdInterview.getInterviewResult().equals("1")) {
//            tdResumeService.updateResumeStatus("3", tdInterview.)
//        }
        update(tdInterview, new LambdaQueryWrapper<TDInterview>().eq(TDInterview::getInterviewId, updateInterviewDto.getInterviewId()));
        return null;
    }

    @Override
    public Object offerList(Integer page, Integer size) {

        return null;
    }
}
