package com.queryx.recruiting_website.domain.dto;

import lombok.Data;

/**
 * @Author：fjj
 * @Date：2025/4/23 11:43
 */
@Data
public class SearchJobDTO extends SearchDTO{
    // 学历
    private String education;
    // 工作性质
    private String nature;
    // 工作地点
    private String area;
    // 薪资范围
    private String salary;
}
