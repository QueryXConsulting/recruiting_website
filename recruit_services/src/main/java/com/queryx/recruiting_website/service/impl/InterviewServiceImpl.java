package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDInterview;
import com.queryx.recruiting_website.domain.TDInterviewDate;
import com.queryx.recruiting_website.domain.vo.InterviewDateVO;
import com.queryx.recruiting_website.domain.vo.InterviewVO;
import com.queryx.recruiting_website.mapper.InterviewDateMapper;
import com.queryx.recruiting_website.mapper.InterviewMapper;
import com.queryx.recruiting_website.mapper.TDJobMapper;
import com.queryx.recruiting_website.service.InterviewService;
import com.queryx.recruiting_website.utils.CommonResp;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InterviewServiceImpl implements InterviewService {

    @Autowired
    private InterviewMapper interviewMapper;

    @Autowired
    private InterviewDateMapper interviewDateMapper;

    @Autowired
    private TDJobMapper jobInfoMapper;

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
    public CommonResp<Boolean>  isAcceptInterview(Long interviewId, String isAccept) {
        if (isAccept == null || interviewId == null || isAccept.trim().isEmpty()) 
            return CommonResp.fail(AppHttpCodeEnum.MISSING_PARAMETERS, false);
        TDInterview interview = interviewMapper.selectById(interviewId);
        if (interview == null) return CommonResp.fail(AppHttpCodeEnum.INTERVIEW_NOT_EXIST, false);
        if (Common.DELETE.equals(interview.getIsDeleted())) 
            return CommonResp.fail(AppHttpCodeEnum.USER_NOT_EXIST, false);
        interview.setInterviewStatus(isAccept);
        return CommonResp.success(interviewMapper.updateById(interview) > 0);
    }





}
