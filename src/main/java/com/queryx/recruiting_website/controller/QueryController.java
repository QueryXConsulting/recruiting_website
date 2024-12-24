package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.service.impl.QueryImpl;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.vo.AttachmentsResumeListVO;
import com.queryx.recruiting_website.vo.InterviewVO;
import com.queryx.recruiting_website.vo.ResumeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class QueryController {

    @Autowired
    private QueryImpl queryUserInfo;

    /**
     * 查询用户在线简历
     * @param id
     * @return
     */
    @GetMapping("/query/online_resume/{id}")
    public CommonResp<ResumeVO> queryOnlineResume(@PathVariable("id") Long id) {
        CommonResp<ResumeVO> resp = null;
        try {
            resp = new CommonResp<>(
                    AppHttpCodeEnum.SUCCESS.getCode(),
                    AppHttpCodeEnum.SUCCESS.getMsg(),
                    queryUserInfo.getOnlineResume(id));
        } catch (Exception e) {
            log.error("用户在线简历查询失败{}", e.getMessage());
            resp = new CommonResp<>(
                    AppHttpCodeEnum.SYSTEM_ERROR.getCode(),
                    AppHttpCodeEnum.SYSTEM_ERROR.getMsg(),
                    null);
        }
        return resp;
    }

    @GetMapping("/query/attachment_resume/list/{id}")
    public CommonResp<List<AttachmentsResumeListVO>> queryResumeAttachments(@PathVariable("id") Long id) {
        CommonResp<List<AttachmentsResumeListVO>> resp = null;
        try {
            resp = new CommonResp<>(
                    AppHttpCodeEnum.SUCCESS.getCode(),
                    AppHttpCodeEnum.SUCCESS.getMsg(),
                    queryUserInfo.getResumeAttachmentList(id));
        } catch (Exception e) {
            log.error("用户附件简历查询失败{}", e.getMessage());
            resp = new CommonResp<>(
                    AppHttpCodeEnum.SYSTEM_ERROR.getCode(),
                    AppHttpCodeEnum.SYSTEM_ERROR.getMsg(),
                    null);
        }
        return resp;
    }

    @GetMapping("/query/interview/{id}")
    public CommonResp<InterviewVO> queryReviewResume(@PathVariable("id") Long id) {
        CommonResp<InterviewVO> resp = null;
        try {
            resp = new CommonResp<>(
                    AppHttpCodeEnum.SUCCESS.getCode(),
                    AppHttpCodeEnum.SUCCESS.getMsg(),
                    queryUserInfo.getInterview(id));
        } catch (Exception e) {
            log.error("用户面试信息查询失败{}", e.getMessage());
            resp = new CommonResp<>(
                    AppHttpCodeEnum.SYSTEM_ERROR.getCode(),
                    AppHttpCodeEnum.SYSTEM_ERROR.getMsg(),
                    null);
        }
        return resp;
    }
}
