package com.queryx.recruiting_website.handler;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import com.queryx.recruiting_website.exception.SystemException;
import com.queryx.recruiting_website.utils.CommonResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SystemException.class)
    public CommonResp sysExceptionHandler(SystemException e) {
        // 打印异常信息
        log.error("出现异常！ {}", e);
        // 从异常对象中获得提示信息封装返回
        return new CommonResp<>(e.getCode(),e.getMessage(),null);

    }

    @ExceptionHandler(Exception.class)
    public CommonResp exceptionHandler(Exception e) {
        // 打印异常信息
        log.error("出现异常！ {}", e);
        // 从异常对象中获得提示信息封装返回
        return new CommonResp<>(AppHttpCodeEnum.SYSTEM_ERROR.getCode(),e.getMessage(),null);
    }


}
