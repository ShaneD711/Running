package com.shaned.running.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应，包含 Token 和用户基本信息
 *
 * 前端保存 token，后续请求在 Header 中携带：Authorization: Bearer <token>
 * Token 有效期 24 小时，调用 /auth/logout 后立即失效
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {

    private String token; // JWT Token，前端需保存
    private UserVO user;  // 用户基本信息，避免登录后再请求一次 /user/me
}
