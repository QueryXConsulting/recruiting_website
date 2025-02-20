package com.queryx.recruiting_website.controller;

import com.queryx.recruiting_website.domain.dto.CategoryDto;
import com.queryx.recruiting_website.domain.dto.JobDetailDto;
import com.queryx.recruiting_website.domain.dto.JobDto;
import com.queryx.recruiting_website.domain.dto.JobNatureDto;
import com.queryx.recruiting_website.service.TDCategoryService;
import com.queryx.recruiting_website.service.TDJobNatureService;
import com.queryx.recruiting_website.service.TDJobService;
import com.queryx.recruiting_website.utils.CommonResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
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
    public CommonResp selectJobList(Integer page, Integer size, String companyName,String jobName
            ,String jobReview,String jobStatus,String jobCategory,String jobNature) {

        return CommonResp.success(jobService.selectJobList(page, size, companyName,jobName,jobReview,jobStatus,jobCategory,jobNature));
    }

    @GetMapping("/selectJobInfo/{jobId}")
    @Operation(summary = "职位信息")
    public CommonResp selectJobInfo(@PathVariable("jobId") Long jobId) {
        return CommonResp.success(jobService.selectJobInfo(jobId));
    }

    @PutMapping("/updateJobInfo")
    @Operation(summary = "职位更新")
    public CommonResp updateJobInfo(@RequestBody JobDetailDto jobDetailDto) {

        return CommonResp.success(jobService.updateJob(jobDetailDto));
    }

    @DeleteMapping("/deleteJob/{jobId}")
    @Operation(summary = "职位删除")
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
    public CommonResp addJob(@RequestBody JobDto jobDto) {
        return CommonResp.success(jobService.addJob(jobDto));
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

    @PutMapping("/updateCategory")
    @Operation(summary = "工种数据更新")
    public CommonResp updateCategory(@RequestBody CategoryDto categoryDto) {
        return CommonResp.success(categoryService.updateCategory(categoryDto));
    }

    @PostMapping("/addCategory")
    @Operation(summary = "添加工种")
    public CommonResp addCategory(@RequestBody CategoryDto categoryDto) {
        return CommonResp.success(categoryService.addCategory(categoryDto));
    }

    @DeleteMapping("/delCategory/{categoryId}")
    @Operation(summary = "删除工种")
    public CommonResp delCategory(@PathVariable("categoryId") Long categoryId) {
        return CommonResp.success(categoryService.delCategory(categoryId));
    }


    @GetMapping("/selectJobNatureList")
    @Operation(summary = "求职类型列表")
    public CommonResp selectJobNatureList(String status) {
        return CommonResp.success(jobNatureService.selectJobNatureList(status));
    }

    @GetMapping("/selectJobNatureInfo/{natureId}")
    @Operation(summary = "求职类型信息")
    public CommonResp selectJobNatureInfo(@PathVariable("natureId") Long jobNatureId) {
        return CommonResp.success(jobNatureService.selectNatureInfo(jobNatureId));
    }

    @PutMapping("/updateJobNatureStatus/{status}/{natureId}")
    @Operation(summary = "求职类型状态更新")
    public CommonResp updateJobNatureStatus(@PathVariable("status") Integer status,@PathVariable("natureId") Long jobNatureId) {
        return CommonResp.success(jobNatureService.updateJobNatureStatus(status,jobNatureId));
    }

    @PutMapping("/updateJobNature")
    @Operation(summary = "求职类型数据更新")
    public CommonResp updateJobNature(@RequestBody JobNatureDto jobNatureDto) {
        return CommonResp.success(jobNatureService.updateJobNature(jobNatureDto));
    }

    @GetMapping("/addJobNature")
    @Operation(summary = "添加求职类型")
    public CommonResp addJobNature(String jobNatureName,String natureStatus) {
        return CommonResp.success(jobNatureService.addJobNature(jobNatureName,natureStatus));
    }

    @DeleteMapping("/delJobNature/{natureId}")
    @Operation(summary = "删除求职类型")
    public CommonResp delJobNature(@PathVariable("natureId") Long jobNatureId) {
        return CommonResp.success(jobNatureService.delJobNature(jobNatureId));
    }

}
