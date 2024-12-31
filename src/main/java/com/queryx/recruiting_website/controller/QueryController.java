package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.domain.vo.JobVO;
import com.queryx.recruiting_website.service.impl.QueryImpl;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.domain.vo.AttachmentsResumeVO;
import com.queryx.recruiting_website.domain.vo.InterviewVO;
import com.queryx.recruiting_website.domain.vo.ResumeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/query")
public class QueryController {

    @Autowired
    private QueryImpl queryUserInfo;

    /**
     * 查询用户在线简历
     *
     * @param id 简历id
     * @return 在线简历信息
     */
    @GetMapping("/resume/online")
    public CommonResp<ResumeVO> queryOnlineResume(@RequestParam("id") Long id) {
        ResumeVO resp;
        try {
            resp = queryUserInfo.getOnlineResume(id);
            if (resp == null) {
                return CommonResp.fail(AppHttpCodeEnum.RESUME_NOT_EXIST, null);
            }
        } catch (Exception e) {
            log.error("用户在线简历查询失败，{}", e.getMessage());
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        return CommonResp.success(resp);
    }

    /**
     * 查询用户附件简历列表
     *
     * @param id 用户id
     * @return 附件简历列表
     * TODO: 附件简历列表查询：上传数量限制
     */
    @GetMapping("/resume/attachments")
    public CommonResp<List<AttachmentsResumeVO>> queryResumeAttachments(@RequestParam("id") Long id) {
        List<AttachmentsResumeVO> resp = null;
        try {
            resp = queryUserInfo.getResumeAttachmentList(id);
            if (resp == null) {
                return CommonResp.fail(AppHttpCodeEnum.RESUME_NOT_EXIST, null);
            }
        } catch (Exception e) {
            log.error("用户附件简历查询失败，{}", e.getMessage());
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, resp);
        }
        return CommonResp.success(resp);
    }

    /**
     * 查询用户面试信息
     *
     * @param id 用户id
     * @return 面试信息
     */
    @GetMapping("/interview")
    public CommonResp<InterviewVO> queryReviewResume(@RequestParam("id") Long id) {
        CommonResp<InterviewVO> resp;
        try {
            resp = CommonResp.success(queryUserInfo.getInterview(id));
        } catch (Exception e) {
            log.error("用户面试信息查询失败，{}", e.getMessage());
            resp = CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        return resp;
    }

    /**
     * 查询招聘岗位信息
     *
     * @param id 岗位id
     * @return 招聘岗位信息
     */
    @GetMapping("jobs")
    public CommonResp<JobVO> queryJob(@RequestParam("id") Long id) {
        JobVO resp;
        try {
            resp = queryUserInfo.getJob(id);
            if (resp == null) {
                log.error("招聘岗位不存在，id={}", id);
                return CommonResp.fail(AppHttpCodeEnum.JOB_NOT_EXIST, null);
            }
        } catch (Exception e) {
            log.error("招聘岗位未知错误，{}", e.getMessage());
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        return CommonResp.success(resp);
    }
}
