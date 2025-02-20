package com.queryx.recruiting_website.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * (TDJob)实体类
 *
 * @author makejava
 * @since 2024-12-23 12:21:50
 */
@Data
@ToString
@Schema(name = "岗位信息")
public class TDJob {

    @TableId(type = IdType.AUTO)
    @Schema(name = "岗位ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long jobId;

    @Schema(name = "公司ID", implementation = Long.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Long companyId;

    @Schema(name = "岗位名称", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobPosition;

    @Schema(name = "岗位描述", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobPositionDescribe;

    @Schema(name = "岗位类别", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobCategory;

    @Schema(name = "岗位地点", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobArea;

    @Schema(name = "招聘人数", implementation = Integer.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer jobPersonNumber;

    @Schema(name = "薪资范围", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobSalary;

    @Schema(name = "学历要求", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobEducation;

    @Schema(name = "工作经验", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobExperience;

    @Schema(name = "联系人名称", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobContact;


    @Schema(name = "发布时间", implementation = Date.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Date jobTime;

    @Schema(name = "浏览量", implementation = Integer.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer jobView;

    @Schema(name = "岗位性质", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobNature;

    @Schema(name = "审核状态", description = "0代表待审核,1代表审核通过", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobReview;

    @Schema(name = "启用状态", description = "0代表关闭,1代表启用", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobStatus;


    @Schema(name = "标志", description = "删除标志，0表示未删除，1表示已删除", implementation = String.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private String delFlag;
}

