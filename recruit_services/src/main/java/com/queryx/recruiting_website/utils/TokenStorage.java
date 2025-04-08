package com.queryx.recruiting_website.utils;


import jakarta.annotation.PostConstruct;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class TokenStorage {
    private static final ConcurrentHashMap<String, Map.Entry<Object, Long>> TOKEN_MAP = new ConcurrentHashMap<>();
    private static final long EXPIRATION_TIME = 8 * 60 * 60 * 1000L;

    public static void addToken(String token, Object object) {
        long expiration = System.currentTimeMillis() + EXPIRATION_TIME;
        TOKEN_MAP.put(token, new AbstractMap.SimpleEntry<>(object, expiration));
    }

    public static Object getUser(String token) {
        Map.Entry<Object, Long> entry = TOKEN_MAP.get(token);
        if (entry != null) {
            long expiration = entry.getValue();
            if (System.currentTimeMillis() < expiration) {
                return entry.getKey();
            } else {
                TOKEN_MAP.remove(token);
            }
        }
        return null;
    }

    public static void removeToken(String token) {
        TOKEN_MAP.remove(token);
    }

    // 初始化
    @PostConstruct
    public void init() {
        scheduleTokenCleanup();
    }

    // 定期清理过期 Token
    public void scheduleTokenCleanup() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            TOKEN_MAP.forEach((token, entry) -> {
                if (entry.getValue() < now) {
                    TOKEN_MAP.remove(token);
                }
            });
        }, 0, 60000, null);
    }
}
