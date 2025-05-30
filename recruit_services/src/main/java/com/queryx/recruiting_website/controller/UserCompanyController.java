package com.queryx.recruiting_website.controller;

import com.alibaba.fastjson2.JSON;
import com.queryx.recruiting_website.domain.TDUser;
import com.queryx.recruiting_website.domain.dto.*;
import com.queryx.recruiting_website.domain.vo.CompanyInfoDto;
import com.queryx.recruiting_website.service.*;
import com.queryx.recruiting_website.service.impl.ConversionService;
import com.queryx.recruiting_website.utils.CommonResp;
import com.queryx.recruiting_website.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.List;


@RestController
@Tag(name = "公司用户模块")
@RequestMapping("/company")
public class UserCompanyController {
    @Resource
    private TDJobService tdJobService;
    @Resource
    private TDUserService tdUserService;
    @Resource
    private TDResumeService tdResumeService;
    @Resource
    private TDCompanyInfoService tdCompanyInfoService;
    @Resource
    private TDCategoryService categoryService;
    @Resource
    private TPRoleService tpRoleService;
    @Resource
    private TDInterviewDateService tdInterviewDateService;
    @Resource
    private InterviewService interviewService;
    @Resource
    private TDOffersService offersService;
    @Resource
    private MaterialService materialService;
    @Resource
    private TDRegistrationService registrationService;
    @Resource
    private ConversionService conversionService;


    @GetMapping("/selectCategory")
    @Operation(summary = "工种分类列表查询")
    public CommonResp selectCategoryList(String categoryName) {
        return CommonResp.success(categoryService.selectCategoryList(1, 100, categoryName, "0"));
    }

    @PostMapping("/updateUserCompany")
    @Operation(summary = "公司员工修改")
    public CommonResp updateUserCompany(@RequestParam(value = "dtoJson") String jsonDto
            , @RequestParam(value = "userAvatar", required = false) MultipartFile userAvatar) throws IOException {
        UserDto userDto = JSON.parseObject(jsonDto, UserDto.class);
        return CommonResp.success(tdUserService.updateUserCompany(userDto, userAvatar));
    }

    @PostMapping("/addUserCompany")
    @Operation(summary = "新增公司员工")
    public CommonResp addUserCompany(@RequestParam(value = "dtoJson") String jsonDto, @RequestParam(value = "userAvatar", required = false) MultipartFile userAvatar) throws IOException {
        UserDto userDto = JSON.parseObject(jsonDto, UserDto.class);
        return CommonResp.success(tdUserService.addUserCompany(userDto, userAvatar));
    }

    @DeleteMapping("/delUserCompany/{userId}")
    @Operation(summary = "删除公司员工")
    public CommonResp delUserCompany(@PathVariable("userId") Long userId) {
        return CommonResp.success(tdUserService.deleteUser(userId));
    }

    @GetMapping("/jobList")
    @Operation(summary = "公司工作列表查询")
    public CommonResp selectJobList(Integer page, Integer size, String jobName
            , String jobReview, String jobCategory) {
        return CommonResp.success(tdJobService.selectCompanyJobList(page, size, jobName, jobReview, jobCategory));
    }

    @GetMapping("/companyRole")
    @Operation(summary = "公司角色列表")
    public CommonResp selectRoleList() {
        return CommonResp.success(tpRoleService.selectRoleList());
    }

    @GetMapping("/userCompanyList")
    @Operation(summary = "公司员工列表查询")
    public CommonResp selectUserCompanyList(Integer page, Integer size, String userName) {
        return CommonResp.success(tdUserService.selectUserCompanyList(page, size, userName));
    }

    @GetMapping("/jobInfo/{jobId}")
    @Operation(summary = "查看职位信息")
    public CommonResp selectJobInfo(@PathVariable("jobId") Long jobId) {
        return CommonResp.success(tdJobService.selectJobInfo(jobId));
    }

    @PutMapping("/updateJob")
    @Operation(summary = "更新职位信息")
    public CommonResp updateJob(@RequestBody JobDetailDto jobDetailDto) {
        return CommonResp.success(tdJobService.updateJob(jobDetailDto));
    }

    @PostMapping("/publishJob")
    @Operation(summary = "发布职位")
    public CommonResp insertJobInfo(@RequestBody JobInsertDto jobInsertDto) {
        return CommonResp.success(tdJobService.insertJobInfo(jobInsertDto));
    }

    @DeleteMapping("/deleteJob/{jobId}")
    @Operation(summary = "删除职位")
    public CommonResp deleteJob(@PathVariable("jobId") Long jobId) {
        return CommonResp.success(tdJobService.deleteJob(jobId));
    }

    @GetMapping("/selectUserInfo")
    @Operation(summary = "查询个人信息")
    public CommonResp selectUserInfo() {
        TDUser tdUser = SecurityUtils.getLoginUser().getTdUser();
        return CommonResp.success(tdUserService.selectUserInfo(tdUser.getUserId(), tdUser.getUserRole()));
    }

    @PutMapping("/updateUserInfo")
    @Operation(summary = "更新个人信息")
    public CommonResp updateUserInfo(@RequestBody UserCompanyDto userCompanyDTO) {
        return CommonResp.success(tdUserService.updateUserCompanyInfo(userCompanyDTO));
    }


    @GetMapping("/selectCompanyInfo/{companyId}")
    @Operation(summary = "查询公司信息")
    public CommonResp selectCompanyInfo(@PathVariable("companyId") Long companyId) {
        return CommonResp.success(tdCompanyInfoService.selectCompanyInfo(companyId));
    }

    @PostMapping("/updateCompanyInfo")
    @Operation(summary = "更新公司信息")
    public CommonResp updateCompanyInfo(
            @RequestParam(value = "dtoJson") String jsonDto,
            @RequestParam(value = "applyFiles", required = false) MultipartFile applyFiles,
            @RequestParam(value = "pdfFiles", required = false) List<MultipartFile> pdfFiles) {
        CompanyInfoDto companyInfoDto = JSON.parseObject(jsonDto, CompanyInfoDto.class);
        return CommonResp.success(tdCompanyInfoService.updateCompanyInfo(companyInfoDto, applyFiles, pdfFiles));
    }


    @GetMapping("/resumeList/{jobId}")
    @Operation(summary = "查询投递的简历列表")
    public CommonResp selectResumeList(@PathVariable("jobId") Long jobId, Integer page, Integer size, String resumeType, String resumeName, String resumeStatus) {
        return CommonResp.success(tdResumeService.selectResumeList(page, size, jobId, resumeType, resumeName, resumeStatus));
    }

    @PostMapping("/selectResume")
    @Operation(summary = "查看简历")
    public CommonResp selectResume(@RequestBody SelectResumeDto selectResumeDto) {
        return CommonResp.success(tdResumeService.selectResume(selectResumeDto));
    }

    @PostMapping("/registerCompany")
    @Operation(summary = "公司注册")
    public CommonResp registerCompany(@RequestBody RegisterCompanyDto registerCompanyDto) {
        return CommonResp.success(tdCompanyInfoService.registerCompany(registerCompanyDto));
    }

    @PostMapping("/updateResumeStatus/{resumeStatus}/{jobResumeId}/{resumeDelete}")
    @Operation(summary = "修改简历投递状态")
    public CommonResp updateResumeStatus(@PathVariable("resumeStatus") String resumeStatus, @PathVariable("jobResumeId") Long jobResumeId, @PathVariable("resumeDelete") String resumeDelete) {
        return CommonResp.success(tdResumeService.updateResumeStatus(resumeStatus, jobResumeId, resumeDelete));
    }

    @PostMapping("/addInterviewDate")
    @Operation(summary = "面试时间设置")
    public CommonResp addInterviewDate(@RequestBody AddInterviewDto addInterviewDto) {
        return CommonResp.success(tdInterviewDateService.addInterviewDate(addInterviewDto));
    }

    @GetMapping("/selectInterviewDate")
    @Operation(summary = "面试时间查询")
    public CommonResp addInterviewDate() {
        return CommonResp.success(tdInterviewDateService.selectInterviewDate());
    }

    @DeleteMapping("/deleteInterviewDate/{interviewDateId}")
    @Operation(summary = "面试时间删除")
    public CommonResp deleteInterviewDate(@PathVariable("interviewDateId") Long interviewDateId) {
        return CommonResp.success(tdInterviewDateService.deleteInterviewDate(interviewDateId));
    }

    @PostMapping("/sendInterview")
    @Operation(summary = "发起邀约")
    public CommonResp sendInterview(@RequestBody SendInterviewDto sendInterviewDto) {
        return CommonResp.success(interviewService.sendInterviewDto(sendInterviewDto));
    }

    @GetMapping("/selectInterviewList")
    @Operation(summary = "查询面试列表")
    public CommonResp selectInterviewList(Integer page, Integer size, Long jobId) {
        return CommonResp.success(interviewService.selectInterviewList(page, size, jobId));
    }

    @PutMapping("/updateInterviewList")
    @Operation(summary = "更新面试数据")
    public CommonResp updateInterviewList(@RequestBody UpdateInterviewDto updateInterviewDto) {
        return CommonResp.success(interviewService.updateInterview(updateInterviewDto));
    }

    @GetMapping("/offerList")
    @Operation(summary = "offer列表")
    public CommonResp offerList(Integer page, Integer size, Long jobId) {
        return CommonResp.success(offersService.offerList(page, size, jobId));
    }

    @GetMapping("/selectOfferTemplate")
    @Operation(summary = "offer模板查询")
    public CommonResp selectOffer() {
        return CommonResp.success(offersService.selectOfferTemplate());
    }


    @PostMapping("/offer/save")
    @Operation(summary = "offer保存")
    public CommonResp offerSave(@RequestBody OfferDataDto offerDataDto) throws IOException {
        return CommonResp.success(offersService.saveOffer(offerDataDto));
    }


    @PostMapping("/uploadOffer")

    @Operation(summary = "上传offer文件")
    public CommonResp uploadOffer(
            @RequestParam("file") MultipartFile pdfFile,
            @RequestParam("offerId") String offerId
    )  {
        return CommonResp.success(conversionService.uploadOffer(pdfFile, offerId));
    }

    @PutMapping("/updateOfferStatus/{offerId}/{status}/{jobId}")
    @Operation(summary = "修改offer状态")
    public CommonResp updateOfferStatus(@PathVariable("offerId") Long offerId, @PathVariable("status") String status, @PathVariable("jobId") Long jobId, Long userId) {
        return CommonResp.success(offersService.updateOfferStatus(offerId, status, jobId, userId));
    }

    @GetMapping("selectMaterial")
    @Operation(summary = "查询材料审核列表")
    public CommonResp selectMaterial(Integer page, Integer size, Long jobId) {
        return CommonResp.success(materialService.selectMaterial(page, size, jobId));
    }

    @GetMapping("selectMaterialDetail")
    @Operation(summary = "查询材料")
    public CommonResp selectMaterialDetail(Long materialId,Long offerId) {
        return CommonResp.success(materialService.selectMaterialDetail(materialId,offerId));
    }

    @PutMapping("updateMaterialStatus")
    @Operation(summary = "更新材料状态")
    public CommonResp updateMaterialStatus(Long materialId, String status) {
        return CommonResp.success(materialService.updateMaterialStatus(materialId, status));
    }

    @GetMapping("selectRegistration")
    @Operation(summary = "查询入职信息列表")
    public CommonResp selectRegistration(Integer page, Integer size, Long jobId, String status) {
        return CommonResp.success(registrationService.selectRegistration(page, size, jobId, status));
    }

    @PutMapping("updateRegistrationStatus")
    @Operation(summary = "更新入职信息状态以及设置入职时间")
    public CommonResp updateRegistrationStatus(Long registrationId, String status, @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return CommonResp.success(registrationService.updateRegistrationStatus(registrationId, status, date));
    }

    @GetMapping("downloadPdf")
    @Operation(summary = "pdf填充下载")
    public CommonResp downloadPdf(Long id) throws IOException {
        return CommonResp.success(registrationService.downloadPdf(id));
    }

    @GetMapping ("/enterpriseFiles/{companyInfoId}")
    @Operation(summary = "企业资质文件查看")
    public CommonResp getEnterpriseFiles(@PathVariable(value = "companyInfoId") Long companyInfoId) throws IOException {
        Map<String, byte[]> files = tdCompanyInfoService.getEnterpriseFiles(companyInfoId);
        return CommonResp.success(files);
    }

    @GetMapping ("/checkInterviewTime")
    @Operation(summary = "检查面试时间是否设置")
    public CommonResp checkInterviewTime() {
        return CommonResp.success(tdCompanyInfoService.checkInterviewTime());
    }

    @GetMapping ("/updateStuffs")
    @Operation(summary = "材料检查状态修改")
    public CommonResp updateStuffs(Long materialId,Integer code,String status) {
        return CommonResp.success(tdCompanyInfoService.updateStuffs(materialId,code,status));
    }

}
