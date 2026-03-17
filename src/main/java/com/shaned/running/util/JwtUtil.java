package com.shaned.running.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT工具类
 *
 * 作用：负责JWT Token的生成和验证。
 *
 * JWT结构（三部分用.分隔）：
 * eyJhbGciOiJIUzI1NiJ9          ← Header（头部）：算法类型
 * .eyJzdWIiOiIxIiwicGhvbmUi...  ← Payload（载荷）：存储的数据
 * .QfjUAPaJKd5QCkBAV6vashcb6h6  ← Signature（签名）：防止篡改
 *
 * Payload中存储的数据：
 * - sub（subject）：用户ID
 * - phone：手机号
 * - iat（issuedAt）：签发时间
 * - exp（expiration）：过期时间（24小时后）
 *
 * @Value 从 application.yml 中读取配置值
 */
@Slf4j
@Component
public class JwtUtil {

    // JWT签名密钥，从配置文件读取，默认值为 running-secret-key-2026-03-16-shaned
    @Value("${jwt.secret:running-secret-key-2026-03-16-shaned}")
    private String secret;

    // Token有效期（毫秒），从配置文件读取，默认86400000ms = 24小时
    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    /**
     * 获取签名密钥
     * 将字符串密钥转换为 SecretKey 对象，用于签名和验证
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成JWT Token
     * 登录成功后调用，将用户ID和手机号写入Token
     *
     * @param userId 用户ID
     * @param phone  手机号
     * @return JWT Token字符串
     */
    public String generateToken(Long userId, String phone) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(userId.toString()) // 将用户ID存入sub字段
                .claim("phone", phone)      // 将手机号存入自定义字段
                .issuedAt(now)              // 签发时间
                .expiration(expiryDate)     // 过期时间
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 用密钥签名
                .compact();
    }

    /**
     * 验证Token是否有效
     * 拦截器中调用，验证请求头中的Token
     *
     * @param token JWT Token字符串
     * @return true=有效，false=无效或已过期
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从Token中获取用户ID
     * 拦截器验证通过后，从Token中取出用户ID，存入ThreadLocal供后续使用
     *
     * @param token JWT Token字符串
     * @return 用户ID
     */
    public Long getUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            log.error("获取用户ID失败: ", e.getMessage());
            return null;
        }
    }

    /**
     * 从Token中获取手机号
     *
     * @param token JWT Token字符串
     * @return 手机号
     */
    public String getPhone(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("phone", String.class);
        } catch (Exception e) {
            log.error("获取手机号失败: {}", e.getMessage());
            return null;
        }
    }
}
