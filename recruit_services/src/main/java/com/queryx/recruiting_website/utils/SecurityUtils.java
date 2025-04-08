package com.queryx.recruiting_website.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.queryx.recruiting_website.constant.Common;
import com.queryx.recruiting_website.domain.LoginAdmin;
import com.queryx.recruiting_website.domain.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static LoginAdmin getLoginAdmin() {
        return (LoginAdmin) getAuthentication().getPrincipal();
    }

    public static LoginUser getLoginUser() {
        return (LoginUser) getAuthentication().getPrincipal();
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static String convertCommonRespToJson(CommonResp commonResp) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(commonResp);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        String[] parts = fileName.split("\\.");
        if (parts.length > 1) {
            return parts[parts.length - 1].toLowerCase(); // 返回扩展名并转为小写
        }
        return "";
    }

    public static boolean isAllowedFileType(String fileExtension) {
        for (String ext : Common.FILE_TYPE) {
            if (ext.equals(fileExtension)) {
                return true;
            }
        }
        return false;
    }

}
