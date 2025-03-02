package com.queryx.recruiting_website.constant;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 常量类
 */
@Component
public class Common {

    // 简历投递表相关常量
    public static final String DELIVER_RESUME_STATUS_DELIVERED = "0";// 已投递
    public static final String DELIVER_RESUME_STATUS_TO_BE_SEEN = "0";// 待查看
    public static final String DELIVER_RESUME_STATUS_VIEWED = "1";// 已查看
    public static final String DELIVER_RESUME_STATUS_TO_BE_INTERVIEWED = "2";// 待面试
    // 面试相关常量
    public static final String INTERVIEW_STATUS_BE_INTERVIEWED = "1";// 待面试
    private static String port;
    private static String ip;

    @Value("${server.port}")
    public void setPort(String serverPort) {
        Common.port = serverPort;
    }

    @Value("${server.address}")
    public void setIp(String serverIp) {
        Common.ip = serverIp;
    }

    public static final String ENTERPRISEREVIEW_ENABLE = "1";
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
    public static final String USER_ROLE_COMPANY = "4";

    /* 招聘岗位相关常量 */
    public static final String JOB_STATUS_ENABLE_OK = "1";
    public static final String JOB_STATUS_ENABLE_NOT_OK = "0";

    // 文件类型
    public static final List<String> FILE_TYPE = Arrays.asList("pdf", "png", "jpg");
    public static final Long COMPANY_USER = 4L;


    public static final Long STUDENT_USER = 5L;


    public static final Long SUPER_ADMIN = 1L;


    public static final Long PARENT_MENU = 1L;


    public static final Long ROOT_MENU = 0L;


    public static final String REVIEW_WAIT = "0";


    public static final String STATUS_CLOSE = "0";


    public static final String STATUS_PUBLISH = "1";


    public static final String RESUME_ONLINE = "0";


    public static final String RESUME_ATTACHMENTS = "1";

    public static String getImgURL(){
        return "http://" + ip + ":" + port;
    }

}
