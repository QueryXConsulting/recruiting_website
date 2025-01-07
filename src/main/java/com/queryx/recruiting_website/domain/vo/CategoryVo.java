package com.queryx.recruiting_website.domain.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class CategoryVo {
    private Long categoryId;
    private String categoryName;
    private String categoryStatus;
}
