package com.queryx.recruiting_website.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.queryx.recruiting_website.domain.dto.SearchCompanyDTO;
import com.queryx.recruiting_website.domain.dto.SearchDTO;
import com.queryx.recruiting_website.domain.dto.SearchJobDTO;
import com.queryx.recruiting_website.domain.vo.*;
import com.queryx.recruiting_website.domain.vo.search.SearchCompanyVO;
import com.queryx.recruiting_website.domain.vo.search.SearchJobVO;
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
//    CommonResp<Page<?>> getSearchList(SearchDTO searchDTO);

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
     * @param jobDTO  搜索条件
     * @return 职位列表
     */
    Page<SearchJobVO> getJobList(SearchJobDTO jobDTO);

    /**
     * 查询职位列表
     *
     * @param companyDTO  搜索条件
     * @return 职位列表
     */
    CommonResp<Page<SearchCompanyVO>> getCompanyList(SearchCompanyDTO companyDTO);

    /**
     * 查询所有简历信息
     *
     * @param id 用户id
     * @return 简历信息
     */
    CommonResp<AllResumeVO> getAllResume(Long id);

    /**
     * 查询所有职位性质
     *
     * @return 职位性质列表
     */
    CommonResp<List<JobNatureVO>> getJobNatureList();
}
