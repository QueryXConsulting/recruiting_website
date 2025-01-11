package com.queryx.recruiting_website.domain.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class RoleListVo {
    private Long roleId;
    private String roleName;
    private String status;
    private Date createTime;
}
