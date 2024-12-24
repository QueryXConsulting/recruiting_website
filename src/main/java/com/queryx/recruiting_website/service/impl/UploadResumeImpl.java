package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.queryx.recruiting_website.domain.TDResumeAttachments;
import com.queryx.recruiting_website.mapper.ResumeAttachmentsMapper;
import com.queryx.recruiting_website.service.UploadResume;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class UploadResumeImpl implements UploadResume {

    // TODO 上传附件简历命名规则
    final String prefix = "resume";
    final String suffix = ".pdf";
    final String filePath = "src/main/resources/resume_files/";

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public Integer handleUploadResume(Long userId, MultipartFile file) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            // 创建本地文件 TODO 附件简历上传路径
            File resumeFile = File.createTempFile(String.format("%s_%d_", prefix, userId), suffix, Paths.get(filePath).toFile());
            // 写入文件
            byte[] fileBytes = file.getBytes();
            fileOutputStream = new FileOutputStream(resumeFile, false);
            fileOutputStream.write(fileBytes);
            // 装配数据
            final TDResumeAttachments tdRS = new TDResumeAttachments();
            // TODO 上传附件简历：数据装配待优化
            tdRS.setUserId(userId);
            tdRS.setFileName(file.getOriginalFilename());
            tdRS.setFileSize((int) (file.getSize() / 1024 / 1024));
            tdRS.setUploadDate(Date.from(Instant.now()));
            tdRS.setFilePath(resumeFile.getPath());
            tdRS.setAttachmentsReview("0");
            tdRS.setIsDeleted("0");
//            new TDResumeAttachments(null, userId, file.getName(), (int) (file.getSize() / 1024 / 1024), Date.from(Instant.now()), resumeFile.getPath(), "0", "0")
            // 插入数据库
            MybatisBatch<TDResumeAttachments> mybatisBatch = new MybatisBatch<>(sqlSessionFactory, List.of(tdRS));
            MybatisBatch.Method<TDResumeAttachments> method = new MybatisBatch.Method<>(ResumeAttachmentsMapper.class);
            // 返回结果
            return mybatisBatch.execute(method.insert()).size();
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }
}
