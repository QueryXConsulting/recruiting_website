package com.queryx.recruiting_website.constant;

public enum Common {
    SUPER_ADMIN("1","超级管理员"),
    NOT_DELETED("0","未删除"),
    DELETED("1","已删除"),
    PARENT_MENU("1","父菜单"),
    ROOT_MENU("0","根菜单"),
    REVIEW_WAIT("0","待审核"),
    REVIEW_SUCCESS("1","审核通过"),
    STATUS_CLOSE("0","关闭"),
    STATUS_PUBLISH("1","发布"),
    STATUS_DISABLE("1","禁用"),
    STATUS_ENABLE("0","启用"),
    RESUME_ONLINE("0","线上简历"),
    RESUME_ATTACHMENTS("1","附件简历");

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
