package com.queryx.recruiting_website.utils;


import com.queryx.recruiting_website.constant.AppHttpCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
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

/*
    public static CommonResp success(Object content) {
        CommonResp commonResp = new CommonResp(AppHttpCodeEnum.SUCCESS.getCode(), AppHttpCodeEnum.SUCCESS.getMsg(), null);
        if (content != null) {
            commonResp.setContent(content);
        }
        return commonResp;
    }

    public static CommonResp fail(int code, String msg) {
        return new CommonResp(code, msg, null);
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
*/
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

