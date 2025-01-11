package com.queryx.recruiting_website.domain.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RoleInfoDto {
    private Long roleId;

    private String roleName;

    private Integer roleSort;

    private List<Long> menuId;
}
