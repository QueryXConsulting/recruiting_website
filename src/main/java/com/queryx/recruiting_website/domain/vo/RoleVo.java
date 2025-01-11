package com.queryx.recruiting_website.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class RoleVo {
    private Long roleId;

    private String roleName;

    private Integer roleSort;

    private List<String> menuName;

}
