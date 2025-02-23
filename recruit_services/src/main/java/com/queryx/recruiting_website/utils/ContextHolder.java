package com.queryx.recruiting_website.utils;

import lombok.Data;

import java.util.Map;

@Data
public class ContextHolder {
    private static Map<String, Object> data;

}
