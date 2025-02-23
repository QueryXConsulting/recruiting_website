package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class UserVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    private String userName;
    private String userPhone;
    private String userRole;
    private String userStatus;
    private Integer userInterviews;
}
