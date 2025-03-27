package com.queryx.recruiting_website.exception;


import com.queryx.recruiting_website.constant.AppHttpCodeEnum;

public class SystemException extends RuntimeException{

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }


    public String getMsg() {
        return msg;
    }


    public SystemException(AppHttpCodeEnum httpCodeEnum) {
        super(httpCodeEnum.getMsg());
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMsg();
    }
}
