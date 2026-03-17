package com.shaned.running.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security 配置类
 *
 * 作用：向Spring容器注册一个密码加密器（PasswordEncoder）。
 * 注册后，其他类可以通过 @Autowired 注入使用。
 *
 * 为什么单独放一个配置类？
 * - PasswordEncoder 是 Spring Security 提供的接口
 * - BCryptPasswordEncoder 是它的实现，使用 BCrypt 算法加密
 * - 通过 @Bean 注册到容器，方便全局复用，不需要每次 new 一个对象
 */
@Configuration
public class SecurityConfig {

    /**
     * 注册密码加密器
     * PasswordUtil 中通过 @Autowired 注入这个 Bean 来加密和验证密码
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}