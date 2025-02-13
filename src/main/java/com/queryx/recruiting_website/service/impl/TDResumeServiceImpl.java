package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.*;
import com.queryx.recruiting_website.domain.dto.SelectResumeDTO;
import com.queryx.recruiting_website.domain.vo.ResumeManageVO;
import com.queryx.recruiting_website.mapper.*;
import com.queryx.recruiting_website.service.TDResumeService;
import com.queryx.recruiting_website.domain.vo.ResumeListVO;
import com.queryx.recruiting_website.domain.vo.ResumeVO;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


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
    @Resource
    private TDUserMapper userMapper;


    @Override
    public Page<ResumeListVO> selectResumeList(Integer page, Integer size, Long companyId) {
        LambdaQueryWrapper<TDJob> jobQueryWrapper = new LambdaQueryWrapper<>();
        jobQueryWrapper.eq(TDJob::getCompanyId, companyId)
                .eq(TDJob::getJobReview, Common.REVIEW_OK);
        List<TDJob> tdJobs = tdJobMapper.selectList(jobQueryWrapper);

        if (tdJobs.isEmpty()) {
            return null;
        }

        List<Long> jobIdLst = tdJobs.stream().map(TDJob::getJobId).toList();
        LambdaQueryWrapper<TDJobResume> jobResumeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        jobResumeLambdaQueryWrapper.in(TDJobResume::getJobId, jobIdLst);

        Page<TDJobResume> tdJobResumes = tdJobResumeMapper.selectPage(new Page<>(page, size), jobResumeLambdaQueryWrapper);
        if (tdJobResumes.getRecords().isEmpty()) {
            return null;
        }
        Page<ResumeListVO> resumeListVOPage = new Page<>(tdJobResumes.getCurrent(), tdJobResumes.getSize(), tdJobResumes.getTotal());
        resumeListVOPage.setRecords(tdJobResumes.getRecords().stream()
                .flatMap(tdJobResume -> tdJobs.stream()
                        .filter(tdJob -> tdJobResume.getJobId().equals(tdJob.getJobId()))
                        .map(tdJob -> {
                            ResumeListVO resumeListVO = new ResumeListVO();
                            resumeListVO.setResumeId(tdJobResume.getResumeId());
                            resumeListVO.setResumeName(tdJobResume.getResumeName());
                            resumeListVO.setResumeType(tdJobResume.getResumeType());
                            resumeListVO.setJobPosition(tdJob.getJobPosition());
                            return resumeListVO;
                        })).toList());


        return resumeListVOPage;
    }

    @Override
    public Object selectResume(SelectResumeDTO selectResumeDTO) {

        if (Common.RESUME_ONLINE.equals(selectResumeDTO.getResumeType())) {
            ResumeVO resumeVO = new ResumeVO();
            TDResume tdResume = tdResumeMapper.selectById(selectResumeDTO.getResumeId());
            BeanUtils.copyProperties(tdResume, resumeVO);
            return resumeVO;
        }

        LambdaQueryWrapper<TDResumeAttachments> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TDResumeAttachments::getResumeAttachmentId, selectResumeDTO.getResumeId())
                .eq(TDResumeAttachments::getIsDeleted, Common.NOT_DELETE);

        TDResumeAttachments resume = tdResumeAttachmentsMapper.selectOne(wrapper);
        return resume.getFilePath();
    }

    @Override
    public Page<ResumeManageVO> selectResumeManage(Integer page, Integer size, String userName, String resumeReview
            , String status, String resumeType) {

        LambdaQueryWrapper<TDUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.like(userName != null, TDUser::getUserName, userName)
                .eq(TDUser::getUserRole, Common.STUDENT_USER);

        List<TDUser> users = userMapper.selectList(userLambdaQueryWrapper);
        if (users == null || users.isEmpty()) return null;

        if (Common.RESUME_ONLINE.equals(resumeType)) {
            // 查找在线简历
            List<Long> userResumeOnlineIds = users.stream().map(TDUser::getResumeId).filter(Objects::nonNull).toList();
            Map<Long, String> resumeIDMap = users.stream().collect(Collectors.toMap(TDUser::getResumeId, TDUser::getUserName));

            LambdaQueryWrapper<TDResume> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(TDResume::getResumeId, userResumeOnlineIds)
                    .eq(resumeReview != null, TDResume::getResumeReview, resumeReview)
                    .eq(status != null, TDResume::getResumeStatus, status);
            Page<TDResume> onlineResumes = tdResumeMapper.selectPage(new Page<>(page, size), lambdaQueryWrapper);
            Page<ResumeManageVO> resumeManageVOPage = new Page<>(onlineResumes.getCurrent(), onlineResumes.getSize(), onlineResumes.getTotal());
            resumeManageVOPage.setRecords(onlineResumes.getRecords().stream().map(tdResume -> {
                ResumeManageVO resumeManageVO = new ResumeManageVO();
                BeanUtils.copyProperties(tdResume, resumeManageVO);
                resumeManageVO.setUserName(resumeIDMap.get(resumeManageVO.getResumeId()));
                resumeManageVO.setResumeType(Common.RESUME_ONLINE);
                return resumeManageVO;
            }).toList());
            return resumeManageVOPage;
        }

        // 查找附件简历
        List<Long> userIds = users.stream().map(TDUser::getUserId).toList();
        Map<Long, String> usersNameMap = users.stream().collect(Collectors.toMap(TDUser::getUserId, TDUser::getUserName));
        LambdaQueryWrapper<TDResumeAttachments> attachmentsWrapper = new LambdaQueryWrapper<>();
        attachmentsWrapper.in(TDResumeAttachments::getUserId, userIds)
                .eq(resumeReview != null, TDResumeAttachments::getAttachmentsReview, resumeReview)
                .eq(TDResumeAttachments::getIsDeleted, Common.NOT_DELETE);
        Page<TDResumeAttachments> attachments = tdResumeAttachmentsMapper.selectPage(new Page<>(page, size), attachmentsWrapper);
        Page<ResumeManageVO> attachmentVOs = new Page<>(attachments.getCurrent(), attachments.getSize(), attachments.getTotal());
        attachmentVOs.setRecords(attachments.getRecords().stream().map(attachment -> {
            ResumeManageVO resumeManageVO = new ResumeManageVO();
            resumeManageVO.setResumeId(attachment.getResumeAttachmentId());
            resumeManageVO.setResumeReview(attachment.getAttachmentsReview());
            resumeManageVO.setUserName(usersNameMap.get(attachment.getUserId()));
            resumeManageVO.setResumeType(Common.RESUME_ATTACHMENTS);
            resumeManageVO.setResumeStatus(attachment.getAttachmentsReview());
            return resumeManageVO;
        }).toList());

        return attachmentVOs;

    }

    @Override
    public Object resumeReview(String review, Long resumeId, String resumeType) {
        if (Common.RESUME_ONLINE.equals(resumeType)) {
            LambdaUpdateWrapper<TDResume> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(TDResume::getResumeId, resumeId)
                    .set(TDResume::getResumeReview, review);
            update(updateWrapper);
            return null;
        }

        LambdaUpdateWrapper<TDResumeAttachments> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TDResumeAttachments::getResumeAttachmentId, resumeId)
                .set(TDResumeAttachments::getAttachmentsReview, review);
        tdResumeAttachmentsMapper.update(updateWrapper);
        return null;
    }


}
