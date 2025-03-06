package com.queryx.recruiting_website.service.impl;

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
import com.queryx.recruiting_website.mapper.TDResumeAttachmentsMapper;
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


    final String timeZone = "Asia/Shanghai";

    @Value("${file.upload-path-resume}")
    private String filePath;

    @Autowired
    private TDResumeMapper resumeMapper;

    @Autowired
    private TDResumeAttachmentsMapper TDResumeAttachmentsMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public Integer insertResumeAttachment(Long userId, MultipartFile file) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            // 创建本地文件
            File resumeFile = File.createTempFile(String.format("%d_", System.currentTimeMillis()), file.getOriginalFilename(), new File(filePath));
            // 写入文件
            byte[] fileBytes = file.getBytes();
            fileOutputStream = new FileOutputStream(resumeFile, false);
            fileOutputStream.write(fileBytes);
            fileOutputStream.flush();// 刷新缓冲区，立即将数据写入文件
            // 装配数据
            final TDResumeAttachments tdRS = new TDResumeAttachments();
            // 附件简历数据装配
            tdRS.setUserId(userId);
            tdRS.setFileName(file.getOriginalFilename());
            tdRS.setFileSize((int) (file.getSize() / StorageUnit.KB));
            tdRS.setUploadDate(Date.from(ZonedDateTime.now(ZoneId.of(timeZone)).toInstant()));
//            String[] strings = filePath.split("/");
//            String s = strings[strings.length - 1] + "/";
//            tdRS.setFilePath(s + resumeFile.getName());
            // TODO 附件简历路径
            tdRS.setFilePath("/" + Common.getUploadFolderName(filePath, "/", resumeFile.getName()));
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
        // 获取附件信息
        TDResumeAttachments resumeAttachment = TDResumeAttachmentsMapper.selectById(raId);

        if (resumeAttachment == null) {
            return 0;
        }
        // TODO 获取文件路径,待测试
        String path = resumeAttachment.getFilePath();
//        int lastIndex = filePath.lastIndexOf("/", filePath.length() - 2);
//        path = path.substring(0, lastIndex);
        path = Common.getUploadFolderPath(filePath, "/", "") + path;
        // 删除本地文件
        File file = new File(path);
        if (file.exists() && file.delete()) {
            // 再删除数据库字段
            return TDResumeAttachmentsMapper.delete(new LambdaUpdateWrapper<TDResumeAttachments>()
                    .set(TDResumeAttachments::getIsDeleted, Common.DELETE)
                    .set(TDResumeAttachments::getAttachmentsReview, Common.REVIEW_OK)
                    .eq(TDResumeAttachments::getResumeAttachmentId, raId));
        } else {
            return 0;
        }
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
