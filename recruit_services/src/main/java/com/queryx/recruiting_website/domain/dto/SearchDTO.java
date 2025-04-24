package com.queryx.recruiting_website.domain.dto;

import lombok.Data;

/**
 * @Author：fjj
 * @Date：2025/3/18 14:38
 */
@Data
public class SearchDTO {

    // 关键字
    private String keyword;
    // 页码
    private Integer page;
    // 每页显示条数
    private Integer size;
    // 搜索类型
//    private SearchType searchType;
    // 排序类型
    private Boolean isAsc;



    public enum SearchType {
        JOB,
        COMPANY;

        SearchType() {
        }
    }
}

