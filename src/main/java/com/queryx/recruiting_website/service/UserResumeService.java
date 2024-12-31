package com.queryx.recruiting_website.service;

import com.queryx.recruiting_website.domain.dto.ResumeDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserResumeService {

    /**
     * 上传附件简历
     *
     * @param userId 用户id
     * @param file   简历文件
     * @return 上传结果
     * @throws IOException 异常
     */
    Integer insertResumeAttachment(Long userId, MultipartFile file) throws IOException;

    /**
     * 删除用户附件简历
     *
     * @param raId 附件简历id
     * @return 删除结果
     */
    Integer deleteResumeAttachment(Long raId);

    /**
     * 更新在线简历
     *
     * @param resumeDTO 简历dto
     * @return 更新结果
     */
    Integer updateResume(ResumeDTO resumeDTO);

}
