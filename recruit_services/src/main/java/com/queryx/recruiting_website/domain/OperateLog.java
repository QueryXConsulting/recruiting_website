package com.queryx.recruiting_website.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_d_operate_log")
public class OperateLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String operateUser;
    private String operateName;
    private String methodName;
    private String params;
    private String returnValue;
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;

}
