package com.shaned.running.controller;

import com.shaned.running.common.Result;
import com.shaned.running.service.UserService;
import com.shaned.running.util.UserContext;
import com.shaned.running.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口
 *
 * GET /api/v1/user/me  获取当前登录用户信息，需携带 Token
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public Result<UserVO> getCurrentUser() {
        Long userId = UserContext.getUserId();
        return Result.success(userService.getCurrentUser(userId));
    }
}
