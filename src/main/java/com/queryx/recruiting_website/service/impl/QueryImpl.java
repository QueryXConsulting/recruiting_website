package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.queryx.recruiting_website.domain.TDResume;
import com.queryx.recruiting_website.domain.TDResumeAttachments;
import com.queryx.recruiting_website.mapper.InterviewMapper;
import com.queryx.recruiting_website.mapper.ResumeAttachmentsMapper;
import com.queryx.recruiting_website.mapper.ResumeMapper;
import com.queryx.recruiting_website.service.QueryService;
import com.queryx.recruiting_website.vo.AttachmentsResumeListVO;
import com.queryx.recruiting_website.vo.InterviewVO;
import com.queryx.recruiting_website.vo.ResumeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class QueryImpl implements QueryService {


    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private ResumeAttachmentsMapper resumeAttachmentsMapper;

    @Autowired
    private InterviewMapper interviewMapper;

    @Override
    public ResumeVO getOnlineResume(Long id) {
        // 构建SQL语句，查询简历信息
        final ResumeVO resumeVO = new ResumeVO();
        LambdaQueryWrapper<TDResume> lqw = new LambdaQueryWrapper<>();
        // TODO: 待优化，查询简历信息: 1. 简历状态 2. 简历是否删除
        final List<TDResume> tdResumes = resumeMapper.selectList(new Page<>(1, 10),
                lqw.eq(TDResume::getResumeId, id)
                        .eq(TDResume::getResumeStatus, 0)
                        .eq(TDResume::getResumeReview, 0));
        // 封装简历返回信息
        tdResumes.forEach(tdResume -> BeanUtils.copyProperties(tdResume, resumeVO));
        tdResumes.forEach(tdResume -> System.out.println(tdResume.toString()));
        return resumeVO;
    }

    @Override
    public List<AttachmentsResumeListVO> getResumeAttachmentList(Long id) {
        // 构建SQL语句，查询附件简历信息
        final AttachmentsResumeListVO attachmentsResumeListVO = new AttachmentsResumeListVO();
        final List<AttachmentsResumeListVO> list =  new ArrayList<>();
        LambdaQueryWrapper<TDResumeAttachments> lqw = new LambdaQueryWrapper<>();
        // TODO: 待优化，查询附件简历信息: 1. 附件状态 2. 附件是否删除
        final List<TDResumeAttachments> resumeList = resumeAttachmentsMapper.selectList(new Page<>(1, 10),
                lqw.eq(TDResumeAttachments::getUserId, id)
                        .eq(TDResumeAttachments::getAttachmentsReview, 1)
                        .eq(TDResumeAttachments::getIsDeleted, 0));
        // TODO 待优化，判断简历是否存在
        if (resumeList.isEmpty()) {
            throw new NullPointerException("附件简历不存在");
        }
        // 封装简历返回信息
        resumeList.forEach(resume -> {
            BeanUtils.copyProperties(resume, attachmentsResumeListVO);
            list.add(attachmentsResumeListVO);
        });
        return list;
    }

    @Override
    public InterviewVO getInterview(Long id) {
        // 构建SQL语句，查询简历信息
        final InterviewVO interviewVO = new InterviewVO();
        interviewMapper.getInterviewsByUserId(id).forEach(data -> BeanUtils.copyProperties(data, interviewVO));
        return interviewVO;
    }
}
