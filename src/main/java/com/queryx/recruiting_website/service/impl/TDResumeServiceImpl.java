package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDJobResume;
import com.queryx.recruiting_website.domain.TDResume;
import com.queryx.recruiting_website.domain.TDResumeAttachments;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.dto.SelectResumeDto;
import com.queryx.recruiting_website.domain.vo.ResumeListVO;
import com.queryx.recruiting_website.domain.vo.ResumeManageVO;
import com.queryx.recruiting_website.domain.vo.ResumeVO;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.mapper.*;
import com.queryx.recruiting_website.service.TDResumeService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    @Value("${file.upload-path-resume}")
    private String resumePath;


    @Override
    public Page<ResumeListVO> selectResumeList(Integer page, Integer size, Long jobId, String resumeType, String resumeName) {
        LambdaQueryWrapper<TDJobResume> jobResumeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        jobResumeLambdaQueryWrapper.eq(TDJobResume::getJobId, jobId)
                .eq(StringUtils.hasText(resumeType), TDJobResume::getResumeType, resumeType)
                .like(StringUtils.hasText(resumeName), TDJobResume::getResumeName, resumeName);

        Page<TDJobResume> tdJobResumes = tdJobResumeMapper.selectPage(new Page<>(page, size), jobResumeLambdaQueryWrapper);
        if (tdJobResumes.getRecords().isEmpty()) {
            return null;
        }
        Page<ResumeListVO> resumeListVOPage = new Page<>(tdJobResumes.getCurrent(), tdJobResumes.getSize(), tdJobResumes.getTotal());
        resumeListVOPage.setRecords(tdJobResumes.getRecords().stream().map(tdJobResume -> {
            ResumeListVO resumeListVO = new ResumeListVO();
            resumeListVO.setResumeId(tdJobResume.getResumeId());
            resumeListVO.setResumeName(tdJobResume.getResumeName());
            resumeListVO.setResumeType(tdJobResume.getResumeType());
            return resumeListVO;
        }).toList());

        return resumeListVOPage;
    }

    @Override
    public Object selectResume(SelectResumeDto selectResumeDto) {
        if (selectResumeDto.getResumeType().equals(Common.RESUME_ONLINE)) {
            ResumeVO resumeVO = new ResumeVO();
            TDResume tdResume = tdResumeMapper.selectById(selectResumeDto.getResumeId());
            BeanUtils.copyProperties(tdResume, resumeVO);
            return resumeVO;
        }
        LambdaQueryWrapper<TDResumeAttachments> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TDResumeAttachments::getResumeAttachmentId, selectResumeDto.getResumeId())
                .eq(TDResumeAttachments::getIsDeleted, Common.NOT_DELETE);
        TDResumeAttachments resume = tdResumeAttachmentsMapper.selectOne(wrapper);
        try {
            String filePath = resume.getFilePath();
            String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
            Path path = Paths.get(resumePath, fileName);
            File file = path.toFile();
            if (!file.exists()) {
                throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
            }
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Page<ResumeManageVO> selectResumeManage(Integer page, Integer size, String userName, String resumeReview
            , String status, String resumeType) {

        LambdaQueryWrapper<TDUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.like(userName != null, TDUser::getUserName, userName)
                .eq(TDUser::getUserRole, Common.STUDENT_USER);

        List<TDUser> users = userMapper.selectList(userLambdaQueryWrapper);
        if (resumeType.equals(Common.RESUME_ONLINE)) {
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
            return resumeManageVO;
        }).toList());

        return attachmentVOs;

    }

    @Override
    public Object resumeReview(String review, Long resumeId, String resumeType) {
        if (resumeType.equals(Common.RESUME_ONLINE)) {
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
