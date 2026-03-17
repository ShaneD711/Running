package com.shaned.running.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户注册请求DTO
 *
 * 作用：接收前端发来的注册请求数据，并对数据进行格式校验。
 * DTO（Data Transfer Object）= 数据传输对象，专门用来接收前端传来的数据。
 *
 * 为什么不直接用 User 实体类接收？
 * - User 包含 id、status、createdAt 等字段，注册时前端不需要传这些
 * - 用 DTO 只接收需要的字段，更安全、更清晰
 *
 * @NotBlank  不能为空
 * @Pattern   正则表达式校验
 * @Size      长度校验
 * 以上注解配合 Controller 中的 @Valid 使用，不符合规则时自动抛出异常
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    // 手机号：不能为空，且必须符合中国手机号格式（1开头，第二位3-9，共11位）
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    // 密码：不能为空，且长度至少6位
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少6位")
    private String password;
}
