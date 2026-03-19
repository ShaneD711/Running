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
 * JWT 工具类：生成、验证、解析 Token
 *
 * Token Payload 包含：用户 ID（sub）、手机号（phone）、签发时间（iat）、过期时间（exp）
 * 签名算法：HS256，密钥和有效期从 application.yml 读取
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret:running-secret-key-2026-03-16-shaned}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 默认 24 小时，单位毫秒
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 登录成功后调用，生成携带用户 ID 和手机号的 Token
    public String generateToken(Long userId, String phone) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("phone", phone)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 验证签名和有效期，任一不通过返回 false
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Token 验证失败: {}", e.getMessage());
            return false;
        }
    }

    // 从 Token 中解析用户 ID，解析失败返回 null
    public Long getUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey()).build()
                    .parseSignedClaims(token).getPayload();
            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            log.error("获取用户 ID 失败: {}", e.getMessage());
            return null;
        }
    }

    public String getPhone(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey()).build()
                    .parseSignedClaims(token).getPayload();
            return claims.get("phone", String.class);
        } catch (Exception e) {
            log.error("获取手机号失败: {}", e.getMessage());
            return null;
        }
    }

    // 获取 Token 剩余有效时间（毫秒），由 UserService.logout() 用于设置 Redis TTL
    public long getRemainingExpiration(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey()).build()
                    .parseSignedClaims(token).getPayload();
            long remaining = claims.getExpiration().getTime() - System.currentTimeMillis();
            return Math.max(remaining, 0);
        } catch (Exception e) {
            return 0;
        }
    }
}
