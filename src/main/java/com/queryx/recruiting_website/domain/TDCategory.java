package com.queryx.recruiting_website.domain;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


@Data
public class TDCategory {




    @TableId
    private Long categoryId;

    private String categoryName;

    private String categoryStatus;

}

