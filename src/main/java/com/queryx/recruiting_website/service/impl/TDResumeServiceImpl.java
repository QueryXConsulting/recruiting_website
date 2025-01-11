package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.*;
import com.queryx.recruiting_website.domain.dto.SelectResumeDto;
import com.queryx.recruiting_website.domain.vo.ResumeManageVo;
import com.queryx.recruiting_website.mapper.*;
import com.queryx.recruiting_website.service.TDResumeService;
import com.queryx.recruiting_website.domain.vo.ResumeListVo;
import com.queryx.recruiting_website.domain.vo.ResumeVo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
    public Page<ResumeListVo> selectResumeList(Integer page, Integer size, Long companyId) {
        LambdaQueryWrapper<TDJob> jobQueryWrapper = new LambdaQueryWrapper<>();
        jobQueryWrapper.eq(TDJob::getCompanyId, companyId)
                .eq(TDJob::getJobReview, Common.REVIEW_SUCCESS.getCode());
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
        Page<ResumeListVo> resumeListVoPage = new Page<>(tdJobResumes.getCurrent(), tdJobResumes.getSize(), tdJobResumes.getTotal());
        resumeListVoPage.setRecords(tdJobResumes.getRecords().stream()
                .flatMap(tdJobResume -> tdJobs.stream()
                        .filter(tdJob -> tdJobResume.getJobId().equals(tdJob.getJobId()))
                        .map(tdJob -> {
                            ResumeListVo resumeListVo = new ResumeListVo();
                            resumeListVo.setResumeId(tdJobResume.getResumeId());
                            resumeListVo.setResumeName(tdJobResume.getResumeName());
                            resumeListVo.setResumeType(tdJobResume.getResumeType());
                            resumeListVo.setJobPosition(tdJob.getJobPosition());
                            return resumeListVo;
                        })).toList());


        return resumeListVoPage;
    }

    @Override
    public Object selectResume(SelectResumeDto selectResumeDto) {

        if (selectResumeDto.getResumeType().equals(Common.RESUME_ONLINE.getCode())) {
            ResumeVo resumeVo = new ResumeVo();
            TDResume tdResume = tdResumeMapper.selectById(selectResumeDto.getResumeId());
            BeanUtils.copyProperties(tdResume, resumeVo);
            return resumeVo;
        }

        LambdaQueryWrapper<TDResumeAttachments> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TDResumeAttachments::getResumeAttachmentId, selectResumeDto.getResumeId())
                .eq(TDResumeAttachments::getIsDeleted, Common.NOT_DELETED.getCode());

        TDResumeAttachments resume = tdResumeAttachmentsMapper.selectOne(wrapper);
        return resume.getFilePath();
    }

    @Override
    public Page<ResumeManageVo> selectResumeManage(Integer page, Integer size, String userName, String resumeReview
            , String status, String resumeType) {

        LambdaQueryWrapper<TDUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.like(userName != null, TDUser::getUserName, userName)
                .eq(TDUser::getUserRole, Common.STUDENT_USER.getCode());

        List<TDUser> users = userMapper.selectList(userLambdaQueryWrapper);
        if (resumeType.equals(Common.RESUME_ONLINE.getCode())) {
            // 查找在线简历
            List<Long> userResumeOnlineIds = users.stream().map(TDUser::getResumeId).toList();
            Map<Long, String> resumeIDMap = users.stream().collect(Collectors.toMap(TDUser::getResumeId, TDUser::getUserName));

            LambdaQueryWrapper<TDResume> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(TDResume::getResumeId, userResumeOnlineIds)
                    .eq(resumeReview != null, TDResume::getResumeReview, resumeReview)
                    .eq(status != null, TDResume::getResumeStatus, status);
            Page<TDResume> onlineResumes = tdResumeMapper.selectPage(new Page<>(page, size), lambdaQueryWrapper);
            Page<ResumeManageVo> resumeManageVoPage = new Page<>(onlineResumes.getCurrent(), onlineResumes.getSize(), onlineResumes.getTotal());
            resumeManageVoPage.setRecords(onlineResumes.getRecords().stream().map(tdResume -> {
                ResumeManageVo resumeManageVo = new ResumeManageVo();
                BeanUtils.copyProperties(tdResume, resumeManageVo);
                resumeManageVo.setUserName(resumeIDMap.get(resumeManageVo.getResumeId()));
                resumeManageVo.setResumeType(Common.RESUME_ONLINE.getCode());
                return resumeManageVo;
            }).toList());
            return resumeManageVoPage;
        }

        // 查找附件简历
        List<Long> userIds = users.stream().map(TDUser::getUserId).toList();
        Map<Long, String> usersNameMap = users.stream().collect(Collectors.toMap(TDUser::getUserId, TDUser::getUserName));
        LambdaQueryWrapper<TDResumeAttachments> attachmentsWrapper = new LambdaQueryWrapper<>();
        attachmentsWrapper.in(TDResumeAttachments::getUserId, userIds)
                .eq(resumeReview != null, TDResumeAttachments::getAttachmentsReview, resumeReview)
                .eq(TDResumeAttachments::getIsDeleted, Common.NOT_DELETED.getCode());
        Page<TDResumeAttachments> attachments = tdResumeAttachmentsMapper.selectPage(new Page<>(page, size), attachmentsWrapper);
        Page<ResumeManageVo> attachmentVos = new Page<>(attachments.getCurrent(), attachments.getSize(), attachments.getTotal());
        attachmentVos.setRecords(attachments.getRecords().stream().map(attachment -> {
            ResumeManageVo resumeManageVo = new ResumeManageVo();
            resumeManageVo.setResumeId(attachment.getResumeAttachmentId());
            resumeManageVo.setResumeReview(attachment.getAttachmentsReview());
            resumeManageVo.setUserName(usersNameMap.get(attachment.getUserId()));
            resumeManageVo.setResumeType(Common.RESUME_ATTACHMENTS.getCode());
            return resumeManageVo;
        }).toList());

        return attachmentVos;

    }

    @Override
    public Object resumeReview(String review, Long resumeId, String resumeType) {
        if (resumeType.equals(Common.RESUME_ONLINE.getCode())) {
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
