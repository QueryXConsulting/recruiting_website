package com.queryx.recruiting_website.domain.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class NatureVo {

    private Long natureId;

    private String jobNatureName;

    private String natureStatus;
}
