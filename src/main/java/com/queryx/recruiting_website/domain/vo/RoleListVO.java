package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.util.Date;

@Data
public class RoleListVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long roleId;
    private String roleName;
    private String status;
    private Integer roleSort;
    private Date createTime;
}
