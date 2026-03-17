package com.shaned.running.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码工具类
 *
 * 作用：封装密码的加密和验证操作，统一管理密码相关逻辑。
 *
 * 为什么用静态方法？
 * - Service层调用时不需要注入PasswordUtil，直接 PasswordUtil.encodePassword() 即可
 * - 更方便使用
 *
 * 为什么不直接 new BCryptPasswordEncoder()？
 * - PasswordEncoder 是通过 SecurityConfig 注册到Spring容器的Bean
 * - 通过构造方法注入，保证整个项目使用同一个实例
 * - 符合Spring的依赖注入规范
 */
@Slf4j
@Component
public class PasswordUtil {

    // 静态变量，通过构造方法注入后赋值，供静态方法使用
    private static PasswordEncoder passwordEncoder;

    /**
     * 构造方法注入 PasswordEncoder
     * Spring容器启动时自动调用，将 SecurityConfig 中注册的 Bean 注入进来
     */
    @Autowired
    public PasswordUtil(PasswordEncoder encoder) {
        PasswordUtil.passwordEncoder = encoder;
    }

    /**
     * 加密密码
     * 注册时调用，将明文密码加密后存入数据库
     * BCrypt每次加密结果都不同（自带随机盐值），但验证时能正确匹配
     *
     * @param password 明文密码
     * @return 加密后的密码（存入数据库）
     */
    public static String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * 验证密码
     * 登录时调用，验证用户输入的密码是否与数据库中的加密密码匹配
     *
     * @param rawPassword     用户输入的明文密码
     * @param encodedPassword 数据库中存储的加密密码
     * @return true=密码正确，false=密码错误
     */
    public static boolean matchPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
