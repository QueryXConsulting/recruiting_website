package com.queryx.recruiting_website.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class UserLoginVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private String token;
    private UserInfoVO userInfoVO;
}
