package com.shaned.running.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaned.running.common.Result;
import com.shaned.running.constant.ErrorCode;
import com.shaned.running.service.UserService;
import com.shaned.running.util.JwtUtil;
import com.shaned.running.util.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 认证过滤器
 *
 * 拦截所有请求，验证 Token 合法性。验证失败直接返回 401，不会到达 Controller。
 *
 * 处理流程：
 *   1. 从请求头 Authorization 提取 Token（格式：Bearer <token>）
 *   2. 没有 Token → 放行，由 SecurityConfig 决定该接口能否访问
 *   3. 有 Token → 验证签名和有效期，失败返回 401
 *   4. 查 Redis 黑名单，命中返回 401（用户已退出登录）
 *   5. 解析用户 ID，写入 UserContext，设置 Security 认证信息，放行
 *   6. 请求结束后清理 UserContext 和 SecurityContext（finally 保证执行）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);
        log.debug("请求路径: {}", request.getRequestURI());

        // 没有 Token，直接放行
        // 注册/登录等公开接口本来就不需要 Token，SecurityConfig 的 permitAll() 会放行
        // 受保护接口没有 Token 时，Security 的 authenticated() 会返回 401
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 验证 Token 签名和有效期
        // 签名错误说明 Token 被篡改，过期说明需要重新登录，两种情况都返回 401
        if (!jwtUtil.validateToken(token)) {
            writeUnauthorized(response);
            return;
        }

        // 查 Redis 黑名单
        // 用户退出登录后 Token 被写入黑名单，即使签名合法也不允许访问
        if (userService.isTokenBlacklisted(token)) {
            writeUnauthorized(response);
            return;
        }

        // 解析用户 ID，validateToken 已通过，这里正常情况不会返回 null
        Long userId = jwtUtil.getUserId(token);
        if (userId == null) {
            writeUnauthorized(response);
            return;
        }

        try {
            // 将用户 ID 存入 ThreadLocal，Controller/Service 层通过 UserContext.getUserId() 取用
            UserContext.setUserId(userId);

            // 告知 Spring Security 当前请求已认证
            // 不设置这个，anyRequest().authenticated() 会拦截请求返回 403
            // authorities 必须传非 null 集合，传 null 会被认为未完全认证
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } finally {
            // 请求结束后必须清理，线程池中线程会被复用
            // 不清理会导致下一个请求读到当前用户的数据
            UserContext.clear();
            SecurityContextHolder.clearContext();
        }
    }

    // 从请求头提取 Token，去掉 "Bearer " 前缀
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    // 写入 401 响应，返回统一的 JSON 格式
    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        Result.error(ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED_MSG)
                )
        );
    }
}
