package com.queryx.recruiting_website.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class JwtUtil {

    // 密钥
    private static final String  SECRET_KEY = "secret-queryX-recruiting_website";

    // 设置过期时间
    public static final Long JWT_TTL = 8 * 60 * 60 * 1000L;// 60 * 60 *1000  一个小时



    // 生成token
    public static <T> String createJWT(Long claims) {

        //指定加密算法
        SecureDigestAlgorithm<SecretKey, SecretKey> algorithm = Jwts.SIG.HS256;
        //生成JWT的时间
        long expMillis = System.currentTimeMillis() + JWT_TTL;
        Date exp = new Date(expMillis);
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        Map<String, Long> map = new HashMap<>();
        map.put("userId",claims);
        return Jwts.builder()
                .signWith(key, algorithm) //设置签名使用的签名算法和签名使用的秘钥
                .expiration(exp) // 截止时间
                .claims(map) //设置自定义负载信息
                .compact();
    }


    // 解析token
    public static Jws<Claims> parseJWT(String token) {

        // 密钥实例
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        return Jwts.parser()
                .verifyWith(key)  // 设置签名的密钥
                .build()
                .parseSignedClaims(token);
    }


}
