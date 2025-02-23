package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

@Data
public class RoleMenuVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long menuId;
    private String menuName;
    private String menuType;
    List<RoleMenuVO> children;
}
