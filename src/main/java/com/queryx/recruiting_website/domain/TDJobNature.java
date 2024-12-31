package com.queryx.recruiting_website.domain;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


@Data
public class TDJobNature {


    @TableId
    private Long natureId;

    private String jobNatureName;

    private String natureStatus;

}

