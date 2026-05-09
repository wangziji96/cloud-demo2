package com.atguigu.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // 加密轮数（log2(12) => 2^12 次，约 0.1~0.2 秒，注册性能友好）
    private static final int LOG_ROUNDS = 12;

    /**
     * 加密密码
     */
    public static String encode(String rawPassword) {
        String salt = BCrypt.gensalt(LOG_ROUNDS);
        return BCrypt.hashpw(rawPassword, salt);
    }

    /**
     * 校验密码
     */
    public static boolean matches(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}