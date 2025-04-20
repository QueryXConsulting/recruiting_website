package com.queryx.recruiting_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.mapper.TDResumeMapper;
import com.queryx.recruiting_website.mapper.TDUserMapper;
import com.queryx.recruiting_website.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserResumeServiceImpl implements UserResumeService {


    final String timeZone = "Asia/Shanghai";

    @Value("${file.upload-path-resume}")
    private String filePath;

    @Autowired
    private TDResumeMapper resumeMapper;

    @Autowired
    private TDUserMapper userMapper;

    @Autowired
    private TDResumeAttachmentsMapper resumeAttachmentsMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public Integer insertResumeAttachment(Long userId, MultipartFile file) throws IOException {
        // 创建本地文件
        File resumeFile = File.createTempFile(String.format("%d_", System.currentTimeMillis()), file.getOriginalFilename(), new File(filePath));
        try (FileOutputStream fileOutputStream = new FileOutputStream(resumeFile, false);) {
            // 写入文件
            fileOutputStream.write(file.getBytes());
            fileOutputStream.flush();// 刷新缓冲区，立即将数据写入文件
            // 装配数据
            final TDResumeAttachments tdRS = new TDResumeAttachments();
            // 附件简历数据装配
            tdRS.setUserId(userId);
            tdRS.setFileName(file.getOriginalFilename());
            tdRS.setFileSize((int) (file.getSize() / StorageUnit.KB));
            tdRS.setUploadDate(Date.from(ZonedDateTime.now(ZoneId.of(timeZone)).toInstant()));
            tdRS.setFilePath("/" + Common.getLastPath(filePath, "/", "/" + resumeFile.getName()));
            tdRS.setAttachmentsReview(Common.REVIEW_OK);
            tdRS.setIsDeleted(Common.NOT_DELETE);
            // 插入数据库
            final MybatisBatch<TDResumeAttachments> mybatisBatch = new MybatisBatch<>(sqlSessionFactory, List.of(tdRS));
            final MybatisBatch.Method<TDResumeAttachments> method = new MybatisBatch.Method<>(TDResumeAttachmentsMapper.class);
            // 返回结果
            return mybatisBatch.execute(method.insert()).size();
        }
    }

    @Override
    public Integer deleteResumeAttachment(Long raId) {
        // 获取附件信息
        LambdaQueryWrapper<TDResumeAttachments> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(TDResumeAttachments::getFilePath);
        wrapper.eq(TDResumeAttachments::getResumeAttachmentId, raId);
        TDResumeAttachments resumeAttachment = resumeAttachmentsMapper.selectOne(wrapper);

        if (resumeAttachment == null) {
            return 0;
        }
        String path = resumeAttachment.getFilePath();
        path = Common.getSplitPath(filePath, "/") + path;
        // 删除本地文件
        File file = new File(path);
        if (file.exists()) {
            // 删除本地文件
            if (!file.delete()) {
                return 0;
            }
        }
        // 再删除数据库字段
        return resumeAttachmentsMapper.deleteById(raId);
    }


    @Override
    public Boolean updateResume(ResumeDTO resumeDTO) {
        TDResume tdResume = new TDResume();
        BeanUtils.copyProperties(resumeDTO, tdResume);
        tdResume.setResumeStatus(Common.STATUS_ENABLE);
//        tdResume.setResumeReview(Common.REVIEW_WAIT);, new LambdaUpdateWrapper<TDResume>()
//                .eq(TDResume::getResumeStatus, Common.STATUS_ENABLE)
//                .eq(TDResume::getResumeId, resumeDTO.getResumeId())
        boolean updated = resumeMapper.insertOrUpdate(tdResume);
        // 更新用户姓名
        LambdaUpdateWrapper<TDUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(TDUser::getUserName, resumeDTO.getResumeName());
        updateWrapper.eq(TDUser::getUserId, SecurityUtils.getLoginUser().getTdUser().getUserId());
        int userUpdated = userMapper.update(updateWrapper);
        if (!updated || userUpdated <= 0) {
            log.error("简历更新失败，简历id:{}", resumeDTO.getResumeId());
            throw new RuntimeException("简历更新失败");
        }
        return true;
    }

    @Override
    public Boolean updateResumeAttachment(Long id, String reviewCode) {
        LambdaUpdateWrapper<TDResumeAttachments> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(TDResumeAttachments::getAttachmentsReview, reviewCode);
        wrapper.eq(TDResumeAttachments::getResumeAttachmentId, id);
        int updated = resumeAttachmentsMapper.update(wrapper);
        if (updated <= 0) {
            log.error("附件简历审核失败，附件id:{},reviewCode:{}", id, reviewCode);
            throw new RuntimeException("附件简历审核失败");
        }
        return true;
    }

}
