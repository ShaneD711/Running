package com.shaned.running.controller;

import com.shaned.running.common.Result;
import com.shaned.running.dto.LoginRequest;
import com.shaned.running.dto.RegisterRequest;
import com.shaned.running.service.UserService;
import com.shaned.running.vo.LoginVO;
import com.shaned.running.vo.UserVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 *
 * 作用：接收注册、登录相关的HTTP请求，调用Service处理，返回结果。
 * Controller 只负责"接收请求 → 调用Service → 返回结果"，不写业务逻辑。
 *
 * @RestController  = @Controller + @ResponseBody，表示返回JSON数据
 * @RequestMapping  统一设置接口前缀，所有接口都以 /api/v1/auth 开头
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    // 注入UserService，调用业务逻辑
    @Autowired
    private UserService userService;

    /**
     * 用户注册接口
     *
     * POST /api/v1/auth/register
     *
     * @Valid 触发 RegisterRequest 中的参数校验注解（@NotBlank、@Pattern等）
     * @RequestBody 将请求体中的JSON自动转换为RegisterRequest对象
     */
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        UserVO userVO = userService.register(request);
        return Result.success("注册成功", userVO);
    }

    /**
     * 用户登录接口
     *
     * POST /api/v1/auth/login
     *
     * 登录成功返回JWT Token，前端需要保存Token，后续请求放在请求头中：
     * Authorization: Bearer {token}
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        LoginVO loginVO = userService.login(request);
        return Result.success("登录成功", loginVO);
    }
}
