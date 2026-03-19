package com.shaned.running.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码工具类：BCrypt 加密和验证
 *
 * 注册时调用 encodePassword() 加密后存库，登录时调用 matchPassword() 验证。
 * BCrypt 每次加密结果不同（随机盐），但 matches() 能正确比对。
 *
 * 静态方法通过构造方法注入 PasswordEncoder，Spring 启动时自动完成注入。
 */
@Slf4j
@Component
public class PasswordUtil {

    private static PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordUtil(PasswordEncoder encoder) {

        PasswordUtil.passwordEncoder = encoder;
    }

    public static String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public static boolean matchPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
