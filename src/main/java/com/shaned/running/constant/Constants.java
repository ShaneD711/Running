package com.shaned.running.constant;

/**
 * 通用常量类
 *
 * 作用：统一定义项目中常用的常量值，避免在代码中写魔法字符串。
 * 好处：修改常量只需改这一个地方，不需要全局搜索替换。
 */
public class Constants {

    // 手机号正则表达式：1开头，第二位3-9，后面9位数字，共11位
    // 用于 RegisterRequest 和 LoginRequest 中的 @Pattern 注解
    public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    // 密码最小长度：至少6位
    public static final Integer PASSWORD_MIN_LENGTH = 6;

    // JWT Token前缀：请求头中Token的格式为 "Bearer eyJhbGci..."
    // 解析Token时需要去掉这个前缀
    public static final String TOKEN_PREFIX = "Bearer ";

    // HTTP请求头中Token的字段名
    // 前端发请求时：Authorization: Bearer eyJhbGci...
    public static final String AUTHORIZATION_HEADER = "Authorization";
}
