package com.queryx.recruiting_website.utils;


import com.queryx.recruiting_website.constant.AppHttpCodeEnum;

public class CommonResp<T> {

    /**
     * 编码
     */
    private Integer code;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 返回泛型数据，自定义类型
     */
    private T content;

    public CommonResp() {
    }

    public CommonResp(Integer code, String message, T content) {
        this.code = code;
        this.message = message;
        this.content = content;
    }

    public CommonResp(T content) {
        this.content = content;
    }

    public static CommonResp success(Object content) {
        CommonResp commonResp = new CommonResp(AppHttpCodeEnum.SUCCESS.getCode(), AppHttpCodeEnum.SUCCESS.getMsg(), null);
        if (content != null) {
            commonResp.setContent(content);
        }
        return commonResp;
    }

    public static CommonResp fail(Object content) {
        return new CommonResp(AppHttpCodeEnum.SUCCESS.getCode(), AppHttpCodeEnum.SUCCESS.getMsg(), null);
    }

    public Integer getcode() {
        return code;
    }

    public void setcode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CommonResp{");
        sb.append("code=").append(code);
        sb.append(", message='").append(message).append('\'');
        sb.append(", content=").append(content);
        sb.append('}');
        return sb.toString();
    }
}
