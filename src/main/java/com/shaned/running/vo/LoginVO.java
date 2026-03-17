package com.shaned.running.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应VO
 *
 * 作用：登录成功后返回给前端的数据，包含 Token 和用户信息。
 *
 * 前端拿到 token 后，后续每次请求都需要在请求头中携带：
 * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
 *
 * 服务器通过验证 token 来确认用户身份，不需要每次都重新登录。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {

    // JWT Token，前端需要保存，后续请求时放在请求头中
    private String token;

    // 用户基本信息，前端用于展示用户名、头像等
    private UserVO user;
}
