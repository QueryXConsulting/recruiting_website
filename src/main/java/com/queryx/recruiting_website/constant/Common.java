package com.queryx.recruiting_website.constant;

public enum Common {

    REVIEW_WAIT("0","待审核"),
    REVIEW_SUCCESS("1","审核通过"),
    STATUS_CLOSE("0","关闭"),
    STATUS_PUBLISH("1","发布");

    String code;
    String msg;

    Common(String code, String errorMessage) {
        this.code = code;
        this.msg = errorMessage;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


}
