package com.shaned.running.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * 作用：统一捕获项目中所有抛出的异常，转换成统一的响应格式返回给前端。
 * 这样就不需要在每个Controller里写try-catch了。
 *
 * @RestControllerAdvice 表示这是一个全局异常处理类，对所有Controller生效
 *
 * 处理三种异常：
 * 1. BusinessException  — 业务异常（手机号已注册、密码错误等）
 * 2. MethodArgumentNotValidException — 参数校验异常（@Valid注解触发）
 * 3. Exception          — 其他所有未知异常（兜底处理）
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * 触发场景：Service层主动抛出 throw new BusinessException(...)
     * 返回示例：{ "code": 1001, "message": "手机号已注册" }
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     * 触发场景：Controller方法参数上有@Valid注解，且参数不符合规则时触发
     * 比如：手机号格式错误、密码长度不足
     * 返回示例：{ "code": 400, "message": "手机号格式错误" }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("参数验证异常: {}", message);
        return Result.error(400, message);
    }

    /**
     * 处理所有其他异常（兜底）
     * 触发场景：数据库连接失败、空指针等未预料到的异常
     * 返回示例：{ "code": 500, "message": "系统异常" }
     * 注意：不把具体错误信息返回给前端，避免泄露系统信息
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统异常");
    }
}
