package com.atguigu.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.*;

public class JwtUtil {

    // 密钥（生产环境请放在配置中心或环境变量中）
    private static final String SECRET = "YourSuperSecretKeyForJwtAtLeast256bits!!";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Token 有效期：2 小时
    private static final long EXPIRATION = 2 * 60 * 60 * 1000;

    /**
     * 生成 Token
     */
    public static String generateToken(String userId, List<String> roles) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION);
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())    // jti，用于黑名单
                .setSubject(userId)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取 Token 的 jti
     */
    public static String getJti(String token) {
        return parseToken(token).getId();
    }

    /**
     * 获取 Token 的过期时间
     */
    public static Date getExpiration(String token) {
        return parseToken(token).getExpiration();
    }

    /**
     * 验证 Token 是否有效（签名与过期）
     */
    public static boolean isValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}