package com.queryx.recruiting_website.constant;

import lombok.Getter;

@Getter
public enum AppHttpCodeEnum {


    SUCCESS(200, "操作成功"),
    // 错误
    NEED_LOGIN(401, "需要登录后操作"),
    USERNAME_EXIST(402, "用户名已存在"),
    PHONE_EXIST(403, "手机号已存在"),
    EMAIL_EXIST(404, "邮箱已存在"),
    NO_OPERATOR_AUTH(405, "无权限操作"),
    REQUIRE_USERNAME(406, "必需填写用户名"),
    FILE_TYPE_ERROR(407, "文件类型错误"),
    FILE_UPLOAD_ERROR(408, "文件上传错误"),
    USERNAME_NOT_NULL(409, "用户名不能为空"),
    NICKNAME_NOT_NULL(410, "昵称不能为空"),
    PASSWORD_NOT_NULL(411, "密码不能为空"),
    EMAIL_NOT_NULL(412, "邮箱不能为空"),
    NICKNAME_EXIST(413, "昵称已存在"),
    LOGIN_ERROR(414, "用户名或密码错误"),
    NO_UPDATE(415, "该菜单不允许修改"),
    JOB_NOT_EXIST(416, "职位不存在"),
    NO_DEL(417, "存在子菜单不允许修改"),
    NO_DELETE_USER(418, "无法修改已经登录的用户"),
    UPDATE_RESUME_ERROR(419, "更新简历失败"),
    DELETE_RESUME_ERROR(420, "删除简历失败"),
    DELIVER_RESUME_FAIL(421, "投递简历失败"),
    NO_USER(422, "不允许修改该用户"),
    RESUME_NOT_EXIST(423, "简历不存在"),
    SYSTEM_ERROR(500, "出现错误"),
    LOGIN_EXPIRED(501,"token已过期"),
    PHONE_NULL(424,"手机号不能为空");


    final int code;
    final String msg;

    AppHttpCodeEnum(int code, String errorMessage) {
        this.code = code;
        this.msg = errorMessage;
    }

}
