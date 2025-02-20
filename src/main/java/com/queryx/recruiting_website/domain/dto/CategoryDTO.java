package com.queryx.recruiting_website.domain.dto;


import lombok.Data;

@Data
public class CategoryDTO {
    private Long categoryId;
    private String categoryName;
    private String categoryStatus;
    private String categoryDescription;
}
