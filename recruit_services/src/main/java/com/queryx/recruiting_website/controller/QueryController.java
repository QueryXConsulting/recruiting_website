package com.queryx.recruiting_website.controller;

import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;
import com.queryx.recruiting_website.domain.vo.*;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.SecurityUtils;
import com.queryx.recruiting_website.domain.dto.SearchDTO;
import com.queryx.recruiting_website.service.QueryService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.queryx.recruiting_website.domain.vo.search.SearchJobVO;
import com.queryx.recruiting_website.domain.vo.search.SearchCompanyVO;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/query")
@Tag(name = "查询模块", description = "用户查询相关接口")
public class QueryController {

    @Autowired
    private QueryService queryUserInfo;

    /**
     * 查询用户所有简历
     *
     * @return 简历列表
     */
    @Operation(summary = "查询用户所有简历", responses = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "485", description = "用户不存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "472", description = "简历不存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @GetMapping("/resume/all")
    public CommonResp<AllResumeVO> queryAllResume() {
        final Long id = SecurityUtils.getLoginUser().getTdUser().getUserId();
        return queryUserInfo.getAllResume(id);
    }

    /**
     * 查询用户在线简历
     *
     * @return 在线简历信息
     */
    @Operation(summary = "查询用户在线简历", responses = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "423", description = "简历不存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @GetMapping("/resume/online")
    public CommonResp<ResumeVO> queryOnlineResume() {
        ResumeVO resp;
        final Long id = SecurityUtils.getLoginUser().getTdUser().getResumeId();
        try {
            resp = queryUserInfo.getOnlineResume(id);
            if (resp == null) {
                log.error("用户在线简历不存在，id={}", id);
                return CommonResp.fail(AppHttpCodeEnum.RESUME_NOT_EXIST, null);
            }
        } catch (Exception e) {
            log.error("用户在线简历查询失败，{}", e.getMessage());
            return CommonResp.fail(AppHttpCodeEnum.SYSTEM_ERROR, null);
        }
        return CommonResp.success(resp);
    }

    /**
     * 查询用户所有附件简历
     *
     * @return 附件简历列表
     * TODO: 附件简历列表查询：上传数量限制
     */
    @Operation(summary = "查询用户所有附件简历", responses = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "423", description = "简历不存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @GetMapping("/resume/attachments")
    public CommonResp<List<AttachmentsResumeVO>> queryResumeAttachments() {
        List<AttachmentsResumeVO> resp = null;
        final Long id = SecurityUtils.getLoginUser().getTdUser().getUserId();
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
     * 查询招聘岗位信息
     *
     * @param id 岗位id
     * @return 招聘岗位信息
     */
    @Operation(summary = "查询招聘岗位信息", parameters = {
            @Parameter(name = "id", description = "岗位id", schema = @Schema(implementation = Long.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "416", description = "招聘岗位不存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @GetMapping("job")
    public CommonResp<JobVO> queryJob(@RequestParam("id") String id) {
        JobVO resp;
        try {
            resp = queryUserInfo.getJob(Long.parseLong(id));
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


    /**
     * 用户搜索接口
     *
     * @param searchDTO 搜索条件
     * @return 搜索结果
     */
    @Operation(summary = "用户搜索接口", parameters = {
            @Parameter(name = "searchDTO", description = "搜索条件", schema = @Schema(implementation = SearchDTO.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "482", description = "缺少参数", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @PostMapping("/search")
    public CommonResp<Page<?>> querySearch(@RequestBody SearchDTO searchDTO) {
        // 校验参数
        if (searchDTO == null) {
            return CommonResp.fail(AppHttpCodeEnum.MISSING_PARAMETERS, null);
        }
        // 查询岗位列表
        return queryUserInfo.getSearchList(searchDTO);
    }


    /**
     * 查询招聘岗位列表
     *
     * @param keyword  关键字
     * @param page     页码
     * @param pageSize 页大小
     * @return 招聘岗位列表
     */
    @Operation(summary = "查询招聘岗位列表", parameters = {
            @Parameter(name = "keyword", description = "关键字", schema = @Schema(implementation = String.class), required = true),
            @Parameter(name = "page", description = "页码", schema = @Schema(implementation = Integer.class), required = true),
            @Parameter(name = "pageSize", description = "页大小", schema = @Schema(implementation = Integer.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "416", description = "招聘岗位不存在", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @GetMapping("/jobs")
    public CommonResp<Page<SearchJobVO>> queryJobList(
            @RequestParam("keyword") String keyword,
            @RequestParam("page") Integer page,
            @RequestParam("pageSize") Integer pageSize,
            @RequestParam(value = "isAsc", required = false, defaultValue = "false") boolean isAsc) {
        // 校验参数
        if (keyword == null) return CommonResp.fail(AppHttpCodeEnum.KEYWORD_NOT_NULL, null);
        if (page == null || pageSize == null) return CommonResp.fail(AppHttpCodeEnum.PAGINATION_NOT_NULL, null);
        // 查询岗位列表
        Page<SearchJobVO> jobList = queryUserInfo.getJobList(keyword, page, pageSize, isAsc);
        // 校验结果
        if (jobList == null) return CommonResp.fail(AppHttpCodeEnum.JOB_NOT_EXIST, null);
        return CommonResp.success(jobList);
    }


    /**
     * 查询公司列表
     *
     * @param keyword  关键字
     * @param page     页码
     * @param pageSize 页大小
     * @return 公司列表
     */
    @Operation(summary = "查询公司列表", parameters = {
            @Parameter(name = "keyword", description = "关键字", schema = @Schema(implementation = String.class), required = true),
            @Parameter(name = "page", description = "页码", schema = @Schema(implementation = Integer.class), required = true),
            @Parameter(name = "pageSize", description = "页大小", schema = @Schema(implementation = Integer.class), required = true)
    }, responses = {
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class))),
            @ApiResponse(responseCode = "500", description = "系统错误", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResp.class)))
    })
    @GetMapping("/companyList")
    public CommonResp<Page<SearchCompanyVO>> queryCompanyList(
            @RequestParam("keyword") String keyword,
            @RequestParam("page") Integer page,
            @RequestParam("pageSize") Integer pageSize,
            @RequestParam(value = "isAsc", required = false, defaultValue = "false") boolean isAsc) {
        // 校验参数
        if (keyword == null) return CommonResp.fail(AppHttpCodeEnum.KEYWORD_NOT_NULL, null);
        if (page == null || pageSize == null) return CommonResp.fail(AppHttpCodeEnum.PAGINATION_NOT_NULL, null);
        // 查询岗位列表
        return queryUserInfo.getCompanyList(keyword, page, pageSize, isAsc);
    }
}
