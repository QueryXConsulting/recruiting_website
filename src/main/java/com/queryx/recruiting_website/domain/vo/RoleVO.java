package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

@Data
public class RoleVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long roleId;

    private String roleName;

    private Integer roleSort;

    private List<Long> menusListId;

}
