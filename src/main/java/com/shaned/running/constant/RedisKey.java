package com.shaned.running.constant;

/**
 * Redis key 前缀常量，所有 Redis key 统一在这里管理
 * 完整 key 格式：前缀 + 业务标识，例如：jwt:blacklist:<token>
 */
public class RedisKey {

    // 退出登录后将 Token 写入黑名单，TTL 与 Token 剩余有效期一致
    public static final String JWT_BLACKLIST = "jwt:blacklist:";
}
