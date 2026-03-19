package com.shaned.running.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * Service 层直接 throw 异常，不需要在 Controller 里写 try-catch。
 * Spring 捕获到异常后自动路由到对应的 @ExceptionHandler 方法，统一转成 Result 返回。
 *
 * 匹配优先级：Spring 优先匹配最具体的类型，Exception 兜底最后匹配。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 业务异常：手机号已注册、密码错误等，Service 层主动抛出
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    // 参数校验失败：@Valid 触发，只返回第一个校验错误的 message
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("参数校验失败: {}", message);
        return Result.error(400, message);
    }

    // 兜底：未预期的系统异常，不把详细信息返回给前端，避免泄露内部实现
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统异常");
    }
}
