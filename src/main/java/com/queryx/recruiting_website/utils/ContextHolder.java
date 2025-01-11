package com.queryx.recruiting_website.utils;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class ContextHolder {
    private static Map<String, Object> data;

}
