package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.*;
import com.queryx.recruiting_website.domain.dto.SendInterviewDto;
import com.queryx.recruiting_website.domain.dto.UpdateInterviewDto;
import com.queryx.recruiting_website.domain.vo.InterviewDateVO;
import com.queryx.recruiting_website.domain.vo.InterviewListVo;
import com.queryx.recruiting_website.domain.vo.InterviewVO;
import com.queryx.recruiting_website.mapper.*;
import com.queryx.recruiting_website.service.InterviewService;
import com.queryx.recruiting_website.service.MessageBoardService;
import com.queryx.recruiting_website.service.TDResumeService;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InterviewServiceImpl extends ServiceImpl<InterviewMapper, TDInterview> implements InterviewService {


    @Autowired
    private InterviewMapper interviewMapper;
    @Autowired
    private InterviewDateMapper interviewDateMapper;
    @Autowired
    private TDJobMapper jobInfoMapper;
    @Resource
    private TDJobMapper jobMapper;
    @Resource
    private TDResumeService tdResumeService;
    @Resource
    private TDJobResumeMapper tdJobResumeMapper;
    @Resource
    private TDOffersMapper offersMapper;
    @Resource
    private MessageBoardService messageBoardService;

    @Override
    public Object sendInterviewDto(SendInterviewDto sendInterviewDto) {
        Long userId = sendInterviewDto.getUserId();
        Long jobId = sendInterviewDto.getJobId();
        LambdaQueryWrapper<TDJobResume> tdJobResumeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tdJobResumeLambdaQueryWrapper.eq(TDJobResume::getUserId, userId)
                .eq(TDJobResume::getJobId, jobId);
        TDJobResume tdJobResume = tdJobResumeMapper.selectOne(tdJobResumeLambdaQueryWrapper);

        TDInterview tdInterview = new TDInterview();
        BeanUtils.copyProperties(sendInterviewDto, tdInterview);
        tdInterview.setUserId(sendInterviewDto.getUserId());
        tdInterview.setCompanyId(SecurityUtils.getLoginUser().getTdUser().getCompanyInfoId());
        tdInterview.setJobId(sendInterviewDto.getJobId());
        tdInterview.setJobResumeId(tdJobResume.getJobResumeId());
        save(tdInterview);
        if (sendInterviewDto.getInterviewType().equals(Common.INTERVIEW_ONLINE)){
            tdInterview.setRoomId(tdInterview.getInterviewId());
        }

        messageBoardService.sendMessage(sendInterviewDto.getUserId(), SecurityUtils.getLoginUser().getUsername() + " 向您发起了面试邀约");
        return null;
    }

    @Override
    public Object selectInterviewList(Integer page, Integer size, Long jobId) {

        LambdaQueryWrapper<TDInterview> tdInterviewLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tdInterviewLambdaQueryWrapper.eq(TDInterview::getCompanyId, SecurityUtils.getLoginUser().getTdUser().getCompanyInfoId())
                .eq(TDInterview::getJobId, jobId)
                .eq(TDInterview::getIsDeleted, Common.NOT_DELETE);
        Page<TDInterview> interviewPage = page(new Page<>(page, size), tdInterviewLambdaQueryWrapper);
        if (interviewPage.getRecords().isEmpty()) {
            return null;
        }

        List<TDJobResume> tdJobResumes = tdJobResumeMapper.selectList(new LambdaQueryWrapper<TDJobResume>().eq(TDJobResume::getJobId, jobId));
        Map<Long, String> resumeMap = tdJobResumes.stream()
                .collect(Collectors.toMap(TDJobResume::getUserId, TDJobResume::getResumeName));

        Page<InterviewListVo> interviewListVoPage = new Page<>(interviewPage.getCurrent(), interviewPage.getSize(), interviewPage.getTotal());
        interviewListVoPage.setRecords(interviewPage.getRecords().stream().map(interview -> {
            InterviewListVo interviewListVo = new InterviewListVo();
            BeanUtils.copyProperties(interview, interviewListVo);
            String resumeName = resumeMap.get(interview.getUserId());
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

        if (tdInterview.getInterviewResult() != null && tdInterview.getInterviewResult().equals("1")) {
            tdResumeService.updateResumeStatus("3", tdInterview.getJobResumeId(), null);
            TDOffers tdOffers = new TDOffers();
            tdOffers.setUserId(updateInterviewDto.getUserId());
            tdOffers.setJobId(updateInterviewDto.getJobId());
            tdInterview.setInterviewDate(new Date());
            offersMapper.insert(tdOffers);
            tdInterview.setJobId(null);
            tdInterview.setUserId(null);
            messageBoardService.sendMessage(updateInterviewDto.getUserId(), "恭喜您通过面试");
        } else if (tdInterview.getInterviewStatus().equals("3")) {
            BeanUtils.copyProperties(updateInterviewDto, tdInterview);
        }

        update(tdInterview, new LambdaQueryWrapper<TDInterview>().eq(TDInterview::getInterviewId, updateInterviewDto.getInterviewId()));
        return null;
    }


    @Override
    public CommonResp<Page<InterviewVO>> getInterviews(Long userId, Integer page, Integer pageSize) {
        // 构建查询条件
        LambdaQueryWrapper<TDInterview> interviewQueryWrapper = new LambdaQueryWrapper<>();
        interviewQueryWrapper.eq(TDInterview::getUserId, userId);
        interviewQueryWrapper.eq(TDInterview::getIsDeleted, Common.NOT_DELETE);
        interviewQueryWrapper.groupBy(TDInterview::getInterviewId);
        System.err.println(interviewQueryWrapper);
        // 执行查询
        Page<TDInterview> interviewPage = interviewMapper.selectPage(new Page<>(page, pageSize), interviewQueryWrapper);
        if (interviewPage == null || interviewPage.getRecords() == null || interviewPage.getRecords().isEmpty())
            return CommonResp.success(null);
        Page<InterviewVO> objectPage = new Page<>(interviewPage.getCurrent(), interviewPage.getSize(), interviewPage.getTotal());
        // 处理查询结果
        List<InterviewVO> interviewVOList = new ArrayList<>();
        for (TDInterview interview : interviewPage.getRecords()) {
            String jobPosition = jobInfoMapper.selectById(interview.getJobId()).getJobPosition();// 岗位名称
            InterviewVO interviewVO = new InterviewVO();
            BeanUtils.copyProperties(interview, interviewVO);
            interviewVO.setCompanyInfoId(interview.getCompanyId());
            interviewVO.setInterviewJob(jobPosition);
            interviewVOList.add(interviewVO);
        }
        objectPage.setRecords(interviewVOList);
        return CommonResp.success(objectPage);
    }


    @Override
    public CommonResp<InterviewVO> getInterview(Long id) {
        // 构建SQL语句，查询简历信息
        LambdaQueryWrapper<TDInterview> interviewQueryWrapper = new LambdaQueryWrapper<>();
        interviewQueryWrapper.eq(TDInterview::getInterviewId, id);
        interviewQueryWrapper.eq(TDInterview::getIsDeleted, Common.NOT_DELETE);
        // 执行查询
        TDInterview tdInterview = interviewMapper.selectOne(interviewQueryWrapper);
        // 处理查询结果
        if (tdInterview == null) return CommonResp.fail(AppHttpCodeEnum.INTERVIEW_NOT_EXIST, null);
        String jobPosition = jobInfoMapper.selectById(tdInterview.getJobId()).getJobPosition();
        InterviewVO interviewVO = new InterviewVO();
        BeanUtils.copyProperties(tdInterview, interviewVO);
        interviewVO.setInterviewJob(jobPosition);
        interviewVO.setCompanyInfoId(tdInterview.getCompanyId());
        return CommonResp.success(interviewVO);
    }

    @Override
    public CommonResp<List<InterviewDateVO>> getInterviewDate(Long id) {
        // 构建SQL语句，查询面试时间信息
        LambdaQueryWrapper<TDInterviewDate> interviewDateQueryWrapper = new LambdaQueryWrapper<>();
        interviewDateQueryWrapper.eq(TDInterviewDate::getCompanyId, id);
        interviewDateQueryWrapper.eq(TDInterviewDate::getIsDeleted, Common.NOT_DELETE);
        // 执行查询
        List<TDInterviewDate> interviewDateList = interviewDateMapper.selectList(interviewDateQueryWrapper);
        // 处理查询结果
        if (interviewDateList == null || interviewDateList.isEmpty()) return CommonResp.success(null);
        List<InterviewDateVO> interviewDateVOList = new ArrayList<>();
        for (TDInterviewDate interviewDate : interviewDateList) {
            InterviewDateVO interviewDateVO = new InterviewDateVO();
            BeanUtils.copyProperties(interviewDate, interviewDateVO);
            interviewDateVOList.add(interviewDateVO);
        }
        return CommonResp.success(interviewDateVOList);
    }

    @Override
    public CommonResp<Boolean> isAcceptInterview(Long interviewId, String isAccept, Date interviewDate) {
        if (isAccept.trim().isEmpty())
            return CommonResp.fail(AppHttpCodeEnum.MISSING_PARAMETERS, false);
        TDInterview interview = interviewMapper.selectById(interviewId);
        if (interview == null) return CommonResp.fail(AppHttpCodeEnum.INTERVIEW_NOT_EXIST, false);
        if (Common.DELETE.equals(interview.getIsDeleted()))
            return CommonResp.fail(AppHttpCodeEnum.USER_NOT_EXIST, false);
        interview.setInterviewStatus(isAccept);
        if (Common.INTERVIEW_STATUS_BE_INTERVIEWED.equals(isAccept)) {
            if (interviewDate == null) {
                return CommonResp.fail(AppHttpCodeEnum.MISSING_PARAMETERS, null);
            }
            interview.setInterviewDate(interviewDate);
        }
        return CommonResp.success(interviewMapper.updateById(interview) > 0);
    }


}
