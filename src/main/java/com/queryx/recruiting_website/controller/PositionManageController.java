package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.domain.LoginUser;
import com.queryx.recruiting_website.domain.TDCategory;
import com.queryx.recruiting_website.domain.dto.CategoryDTO;
import com.queryx.recruiting_website.domain.dto.JobDetailDTO;
import com.queryx.recruiting_website.domain.dto.JobDTO;
import com.queryx.recruiting_website.domain.dto.JobNatureDTO;
import com.queryx.recruiting_website.service.TDCategoryService;
import com.queryx.recruiting_website.service.TDJobNatureService;
import com.queryx.recruiting_website.service.TDJobService;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Tag(name = "管理员模块下职位相关数据管理")
public class PositionManageController {
    @Resource
    private TDJobService jobService;
    @Resource
    private TDCategoryService categoryService;
    @Resource
    private TDJobNatureService jobNatureService;

    @GetMapping("/selectJobList")
    @Operation(summary = "职位列表")
    public CommonResp selectJobList(Integer page, Integer size, Long companyId,String jobName,String jobReview) {
        return CommonResp.success(jobService.selectJobList(page,size,companyId,jobName,jobReview,null));
    }

    @GetMapping("/selectJobInfo/{jobId}")
    @Operation(summary = "职位信息")
    @PreAuthorize("hasPermission(null ,'system:job:query')")
    public CommonResp selectJobInfo(@PathVariable("jobId") Long jobId) {
        return CommonResp.success(jobService.selectJobInfo(jobId));
    }

    @PutMapping("/updateJobInfo")
    @Operation(summary = "职位更新")
    @PreAuthorize("hasPermission(null ,'system:job:edit')")
    public CommonResp updateJobInfo(@RequestBody JobDetailDTO jobDetailDTO) {

        return CommonResp.success(jobService.updateJob(jobDetailDTO));
    }

    @DeleteMapping("/deleteJob/{jobId}")
    @Operation(summary = "职位删除")
    @PreAuthorize("hasPermission(null ,'system:job:remove')")
    public CommonResp deleteJob(@PathVariable("jobId") Long jobId) {
        return CommonResp.success(jobService.deleteJob(jobId));
    }

    @GetMapping("/jobReview/{review}/{jobId}")
    @Operation(summary = "职位审核")
    public CommonResp jobReview(@PathVariable("review") String review,@PathVariable("jobId") Long jobId) {
        return CommonResp.success(jobService.jobReview(review,jobId));
    }

    @PostMapping("/addJob")
    @Operation(summary = "添加工作")
    @PreAuthorize("hasPermission(null ,'system:job:add')")
    public CommonResp addJob(@RequestBody JobDTO jobDTO) {
        return CommonResp.success(jobService.addJob(jobDTO));
    }

    @GetMapping("/selectCategory")
    @Operation(summary = "工种分类列表查询")
    public CommonResp selectCategoryList(Integer page,Integer size,String categoryName,String status) {
        return CommonResp.success(categoryService.selectCategoryList(page,size,categoryName,status));
    }

    @PutMapping("/updateCategoryStatus/{status}/{categoryId}")
    @Operation(summary = "工种状态更新")
    public CommonResp updateCategoryStatus(@PathVariable("status") Integer status,@PathVariable("categoryId") Long categoryId) {
        return CommonResp.success(categoryService.updateCategoryStatus(status,categoryId));
    }

    @PostMapping("/updateCategory")
    @Operation(summary = "工种数据更新")
    @PreAuthorize("hasPermission(null ,'system:category:edit')")
    public CommonResp updateCategory(@RequestBody CategoryDTO categoryDTO) {
        return CommonResp.success(categoryService.updateCategory(categoryDTO));
    }

    @GetMapping("/addCategory")
    @Operation(summary = "添加工种")
    @PreAuthorize("hasPermission(null ,'system:category:add')")
    public CommonResp addCategory(String categoryName) {
        return CommonResp.success(categoryService.addCategory(categoryName));
    }

    @DeleteMapping("/delCategory/{categoryId}")
    @Operation(summary = "删除工种")
    @PreAuthorize("hasPermission(null ,'system:category:remove')")
    public CommonResp delCategory(@PathVariable("categoryId") Long categoryId) {
        return CommonResp.success(categoryService.delCategory(categoryId));
    }


    @GetMapping("/selectJobNatureList")
    @Operation(summary = "求职类型列表")
    public CommonResp selectJobNatureList() {
        return CommonResp.success(jobNatureService.selectJobNatureList());
    }

    @GetMapping("/selectJobNatureInfo/{jobNatureId}")
    @Operation(summary = "求职类型信息")
    @PreAuthorize("hasPermission(null ,'system:jobType:query')")
    public CommonResp selectJobNatureInfo(@PathVariable("jobNatureId") Long jobNatureId) {
        return CommonResp.success(jobNatureService.selectNatureInfo(jobNatureId));
    }

    @PutMapping("/updateJobNatureStatus/{status}/{jobNatureId}")
    @Operation(summary = "求职类型状态更新")
    public CommonResp updateJobNatureStatus(@PathVariable("status") Integer status,@PathVariable("jobNatureId") Long jobNatureId) {
        return CommonResp.success(jobNatureService.updateJobNatureStatus(status,jobNatureId));
    }

    @PutMapping("/updateJobNature")
    @Operation(summary = "求职类型数据更新")
    @PreAuthorize("hasPermission(null ,'system:jobType:edit')")
    public CommonResp updateJobNature(@RequestBody JobNatureDTO jobNatureDTO) {
        return CommonResp.success(jobNatureService.updateJobNature(jobNatureDTO));
    }

    @GetMapping("/addJobNature")
    @Operation(summary = "添加求职类型")
    @PreAuthorize("hasPermission(null ,'system:jobType:add')")
    public CommonResp addJobNature(String jobNatureName) {
        return CommonResp.success(jobNatureService.addJobNature(jobNatureName));
    }

    @DeleteMapping("/delJobNature/{jobNatureId}")
    @Operation(summary = "删除求职类型")
    @PreAuthorize("hasPermission(null ,'system:jobType:remove')")
    public CommonResp delJobNature(@PathVariable("jobNatureId") Long jobNatureId) {
        return CommonResp.success(jobNatureService.delJobNature(jobNatureId));
    }

}
