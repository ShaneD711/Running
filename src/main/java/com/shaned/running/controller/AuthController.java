package com.shaned.running.controller;

import com.shaned.running.common.Result;
import com.shaned.running.dto.LoginRequest;
import com.shaned.running.dto.RegisterRequest;
import com.shaned.running.service.UserService;
import com.shaned.running.vo.LoginVO;
import com.shaned.running.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口：注册、登录、退出登录
 *
 * POST /api/v1/auth/register  注册，返回用户信息
 * POST /api/v1/auth/login     登录，返回 Token + 用户信息
 * POST /api/v1/auth/logout    退出登录，Token 写入 Redis 黑名单立即失效
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 注册
     * @param request
     * @return
     */
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        UserVO userVO = userService.register(request);
        return Result.success("注册成功", userVO);
    }

    /**
     * 登录
     * @param request
     * @return
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        LoginVO loginVO = userService.login(request);
        return Result.success("登录成功", loginVO);
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    // 不传 Token 也返回成功（幂等），Token 失效由 JwtAuthFilter 的黑名单检查保证
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            userService.logout(bearer.substring(7));
        }
        return Result.success("退出成功", null);
    }
}
