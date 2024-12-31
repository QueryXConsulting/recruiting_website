package com.queryx.recruiting_website.utils;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
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

    public static <T> CommonResp<T> success(T content) {
        CommonResp<T> resp = new CommonResp<>(content);
        resp.setCode(AppHttpCodeEnum.SUCCESS.getCode());
        resp.setMessage(AppHttpCodeEnum.SUCCESS.getMsg());
        return resp;
    }

    public static <T> CommonResp<T> fail(AppHttpCodeEnum ahe, T content) {
        CommonResp<T> resp = new CommonResp<>(content);
        resp.setCode(ahe.getCode());
        resp.setMessage(ahe.getMsg());
        return resp;
    }
}