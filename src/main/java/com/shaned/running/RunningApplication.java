package com.shaned.running;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Running - 跑步装备分享社区平台
 *
 * 项目结构：
 *
 * controller  接口层      接收HTTP请求，调用Service，返回Result
 * service     业务层      核心业务逻辑（注册、登录、发布内容等）
 * mapper      数据层      MyBatisPlus操作数据库，继承BaseMapper自动生成SQL
 * entity      实体类      对应数据库表，不对外暴露（含password等敏感字段）
 * dto         请求对象    接收前端传来的参数，带@Valid校验注解
 * vo          响应对象    返回给前端的数据，过滤掉敏感字段
 * config      配置类      Spring Security、密码加密器等框架配置
 * interceptor 拦截器      JWT Token验证，保护需要登录的接口
 * common      通用类      统一响应格式Result、全局异常处理、业务异常
 * constant    常量类      错误码ErrorCode、通用常量Constants
 * util        工具类      JWT生成验证、密码加密、用户上下文
 *
 * 请求流程：
 * 前端请求 → JwtAuthFilter验证Token → Controller → Service → Mapper → 数据库
 *
 * 当前版本：v0.1.0
 * - 用户注册登录（手机号 + BCrypt密码 + JWT Token）
 */
@SpringBootApplication
public class RunningApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunningApplication.class, args);
    }

}
