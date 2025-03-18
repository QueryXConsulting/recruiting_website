package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.queryx.recruiting_website.domain.dto.SearchDTO;
import com.queryx.recruiting_website.domain.vo.*;
import com.queryx.recruiting_website.domain.vo.search.SearchCompanyVO;
import com.queryx.recruiting_website.domain.vo.search.SearchJobVO;
import com.queryx.recruiting_website.domain.vo.search.SearchResultVO;
import com.queryx.recruiting_website.utils.CommonResp;

import java.util.List;

public interface QueryService {

    /**
     * 查询在线简历信息
     *
     * @param id 简历id
     * @return 简历信息
     */
    ResumeVO getOnlineResume(Long id);

    /**
     * 查询用户所有附件简历
     *
     * @param id 用户id
     * @return 简历信息
     */
    List<AttachmentsResumeVO> getResumeAttachmentList(Long id);

    /**
     * 查询职位列表
     *
     * @param searchDTO 搜索条件
     * @return 职位列表
     */
    CommonResp<Page<?>> getSearchList(SearchDTO searchDTO);

    /**
     * 查询职位信息
     *
     * @param id 职位id
     * @return 职位信息
     */
    JobVO getJob(Long id);

    /**
     * 查询职位列表
     *
     * @param keyword  关键字
     * @param page     页码
     * @param pageSize 页大小
     * @return 职位列表
     */
    Page<SearchJobVO> getJobList(String keyword, Integer page, Integer pageSize);

    /**
     * 查询职位列表
     *
     * @param keyword  关键字
     * @param page     页码
     * @param pageSize 页大小
     * @return 职位列表
     */
    CommonResp<Page<SearchCompanyVO>> getCompanyList(String keyword, Integer page, Integer pageSize);

    /**
     * 查询所有简历信息
     *
     * @param id 用户id
     * @return 简历信息
     */
    CommonResp<AllResumeVO> getAllResume(Long id);

}
