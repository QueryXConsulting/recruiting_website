package com.queryx.recruiting_website.constant;

import lombok.Getter;

@Getter
public enum AppHttpCodeEnum {


    SUCCESS(200, "操作成功"),

    // 服务端错误
    SYSTEM_ERROR(500, "出现错误"),

    // 客户端错误
    NEED_LOGIN(450, "需要登录后操作"),
    USER_EXIST(451, "用户已存在"),
    PHONE_EXIST(452, "手机号已存在"),
    EMAIL_EXIST(453, "邮箱已存在"),
    NO_OPERATOR_AUTH(454, "无权限操作"),
    REQUIRE_USERNAME(455, "必需填写用户名"),
    FILE_TYPE_ERROR(456, "文件类型错误"),
    FILE_UPLOAD_ERROR(457, "文件上传错误"),
    USERNAME_NOT_NULL(458, "用户名不能为空"),
    NICKNAME_NOT_NULL(459, "昵称不能为空"),
    PASSWORD_NOT_NULL(460, "密码不能为空"),
    EMAIL_NOT_NULL(461, "邮箱不能为空"),
    NICKNAME_EXIST(462, "昵称已存在"),
    LOGIN_ERROR(463, "用户名或密码错误"),
    NO_UPDATE(464, "该菜单不允许修改"),
    JOB_NOT_EXIST(465, "职位不存在"),
    NO_DEL(466, "存在子菜单不允许修改"),
    NO_DELETE_USER(467, "无法修改已经登录的用户"),
    UPDATE_RESUME_ERROR(468, "更新简历失败"),
    DELETE_RESUME_ERROR(469, "删除简历失败"),
    DELIVER_RESUME_FAIL(470, "投递简历失败"),
    NO_USER(471, "不允许修改该用户"),
    RESUME_NOT_EXIST(472, "在线简历不存在"),

    LOGIN_EXPIRED(473, "token已过期"),
    PHONE_NULL(474, "手机号不能为空"),
    SUPER_ADMIN(475, "超级管理员不允许修改"),
    PHONE_OR_EMAIL_ILLEGAL(476, "手机号或邮箱输入不合法"),
    INTERVIEW_NOT_EXIST(477, "面试不存在"),
    USERNAME_EXIST(478, "用户名已存在"),
    USERNAME_NOT_EXIST(479, "用户名不存在");


    final int code;
    final String msg;

    AppHttpCodeEnum(int code, String errorMessage) {
        this.code = code;
        this.msg = errorMessage;
    }


}

