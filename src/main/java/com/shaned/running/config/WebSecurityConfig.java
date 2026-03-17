package com.shaned.running.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Web安全配置类
 *
 * 作用：配置哪些接口需要登录才能访问，哪些接口可以直接访问。
 *
 * 核心配置说明：
 * 1. 关闭CSRF（跨站请求伪造防护）
 *    - 前后端分离项目使用JWT认证，不需要CSRF防护
 *    - CSRF主要用于传统的Session认证方式
 *
 * 2. 关闭Session（无状态认证）
 *    - 使用JWT Token认证，服务器不需要存储Session
 *    - 每次请求都通过Token验证身份
 *
 * 3. 接口权限配置
 *    - /api/v1/auth/** 注册、登录接口：所有人可访问（不需要Token）
 *    - 其他所有接口：需要登录（携带有效Token）才能访问
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 关闭CSRF防护（前后端分离项目不需要）
                .csrf().disable()
                // 设置无状态Session（使用JWT，不需要Session）
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                // 注册、登录接口不需要Token，直接放行
                .requestMatchers("/api/v1/auth/**").permitAll()
                // 其他所有接口都需要登录（携带有效Token）
                .anyRequest().authenticated();

        return http.build();
    }
}
