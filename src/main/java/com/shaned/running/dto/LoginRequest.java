package com.shaned.running.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录请求DTO
 *
 * 作用：接收前端发来的登录请求数据。
 * 登录只需要手机号和密码，不需要其他字段。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    // 手机号：不能为空，且必须符合手机号格式
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    // 密码：不能为空（登录时不校验长度，只验证是否匹配）
    @NotBlank(message = "密码不能为空")
    private String password;
}
