package com.queryx.recruiting_website.domain.vo.search;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 搜索工作VO类
 *
 * @author fjj
 * @since 2025-3-18 16:21:10
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SearchJobVO extends SearchResultVO {
    // 岗位id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long jobId;
    // 关联企业
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
    // 工作职位名称
    private String jobPosition;
    // 工作职位描述
    private String companyName;
    // 工资
    private String jobSalary;
    // 教育程度
    private String jobEducation;
    // 工作经验
    private String jobExperience;
    // 岗位创建时间
    private LocalDateTime jobTime;
    // 工作性质
    private String jobNature;

}

