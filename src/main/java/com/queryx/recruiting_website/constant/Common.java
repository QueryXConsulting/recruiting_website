package com.queryx.recruiting_website.constant;

/*
public enum Common {
    COMPANY_USER("4","公司用户"),
    STUDENT_USER("5","学生用户"),
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

*/

/**
 * 常量类
 */
public class Common {
    // 启用
    public static final String STATUS_ENABLE = "0";
    // 禁用
    public static final String STATUS_DISABLE = "1";
    // 审核通过
    public static final String REVIEW_OK = "1";
    // 待审核
    public static final String REVIEW_NOT_OK = "0";
    // 已删除
    public static final String DELETE = "1";
    // 未删除
    public static final String NOT_DELETE = "0";
    /* 在线简历相关常量 */
    /* 附件简历相关常量 */
    /* 用户相关常量 */
    public static final String USER_ROLE_GENERAL = "0";
    public static final String USER_ROLE_COMPANY = "1";

    /* 招聘岗位相关常量 */
    public static final String JOB_STATUS_ENABLE_OK = "1";
    public static final String JOB_STATUS_ENABLE_NOT_OK = "0";


}
