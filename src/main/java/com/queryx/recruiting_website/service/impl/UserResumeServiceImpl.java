package com.queryx.recruiting_website.service.impl;

import com.queryx.recruiting_website.mapper.TDResumeAttachmentsMapper;
import com.queryx.recruiting_website.mapper.TDResumeMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.apache.ibatis.session.SqlSessionFactory;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.TDResume;
import org.springframework.web.multipart.MultipartFile;
import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import org.springframework.beans.factory.annotation.Value;
import com.queryx.recruiting_website.constant.StorageUnit;
import com.queryx.recruiting_website.domain.dto.ResumeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import com.queryx.recruiting_website.service.UserResumeService;
import com.queryx.recruiting_website.domain.TDResumeAttachments;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserResumeServiceImpl implements UserResumeService {

    // 上传附件简历命名前缀及后缀
    final String prefix = "resume";
    final String suffix = ".pdf";

    final String timeZone = "Asia/Shanghai";

    @Value("${file.upload-path-resume}")
    private String filePath;

    @Autowired
    private TDResumeMapper resumeMapper;

    @Autowired
    private TDResumeAttachmentsMapper resumeAttachmentsMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public Integer insertResumeAttachment(Long userId, MultipartFile file) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            // 创建本地文件 TODO 附件简历命名规则
            File resumeFile = File.createTempFile(String.format("%s_%d_", prefix, userId), suffix, Paths.get(filePath).toFile());
            // 写入文件
            byte[] fileBytes = file.getBytes();
            fileOutputStream = new FileOutputStream(resumeFile, false);
            fileOutputStream.write(fileBytes);
            fileOutputStream.flush();// 刷新缓冲区，立即将数据写入文件
            // 装配数据
            final TDResumeAttachments tdRS = new TDResumeAttachments();
            // TODO 上传附件简历：数据装配待优化
            tdRS.setUserId(userId);
            tdRS.setFileName(file.getOriginalFilename());
            tdRS.setFileSize((int) (file.getSize() / StorageUnit.KB));
            tdRS.setUploadDate(Date.from(ZonedDateTime.now(ZoneId.of(timeZone)).toInstant()));
            tdRS.setFilePath(resumeFile.getPath());
            tdRS.setAttachmentsReview(Common.REVIEW_OK);
            tdRS.setIsDeleted(Common.NOT_DELETE);
            // 插入数据库
            final MybatisBatch<TDResumeAttachments> mybatisBatch = new MybatisBatch<>(sqlSessionFactory, List.of(tdRS));
            final MybatisBatch.Method<TDResumeAttachments> method = new MybatisBatch.Method<>(TDResumeAttachmentsMapper.class);
            // 返回结果
            return mybatisBatch.execute(method.insert()).size();
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    @Override
    public Integer deleteResumeAttachment(Long raId) {
        // TODO 删除附件简历: 文件是否删除？
        return resumeAttachmentsMapper.update(new LambdaUpdateWrapper<TDResumeAttachments>()
                .set(TDResumeAttachments::getIsDeleted, Common.DELETE)
                .set(TDResumeAttachments::getAttachmentsReview, Common.REVIEW_OK)
                .eq(TDResumeAttachments::getResumeAttachmentId, raId));
    }

    @Override
    public Integer updateResume(ResumeDTO resumeDTO) {
        TDResume tdResume = new TDResume();
        BeanUtils.copyProperties(resumeDTO, tdResume);
        return resumeMapper.update(tdResume, new LambdaUpdateWrapper<TDResume>()
                .eq(TDResume::getResumeStatus, Common.STATUS_ENABLE)
                .eq(TDResume::getResumeId, resumeDTO.getResumeId()));
    }
}
