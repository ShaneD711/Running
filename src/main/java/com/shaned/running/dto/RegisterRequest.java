package com.shaned.running.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册请求参数
 *
 * Controller 方法参数加 @Valid 后，Spring 自动触发字段上的校验注解。
 * 校验失败抛出 MethodArgumentNotValidException，由 GlobalExceptionHandler 返回 400。
 *
 * 不直接用 User 实体接收的原因：User 包含 id、status 等字段，注册时前端不应传这些。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    // 正则：1 开头，第二位 3-9，后跟 9 位数字，共 11 位
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少6位")
    private String password;
}
