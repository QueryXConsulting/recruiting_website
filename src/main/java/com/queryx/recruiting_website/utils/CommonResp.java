package com.queryx.recruiting_website.utils;

import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Schema(name = "响应返回数据对象")
public class CommonResp<T> {

    @Schema(
            title = "响应码",
            implementation = Integer.class,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer code;

    @Schema(
            title = "响应信息",
            implementation = String.class,
            accessMode = Schema.AccessMode.READ_ONLY,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String message;

    @Schema(title = "content", description = "响应数据", implementation = Object.class, accessMode = Schema.AccessMode.READ_ONLY)
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