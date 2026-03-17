package com.shaned.running.common;

/**
 * 业务异常类
 *
 * 作用：当业务逻辑出现问题时，抛出这个异常。
 * 比如：手机号已注册、密码错误、用户不存在等。
 *
 * 为什么要自定义异常？
 * - Java自带的异常（如RuntimeException）没有code字段
 * - 我们需要把错误码一起传递给GlobalExceptionHandler
 * - GlobalExceptionHandler捕获后，返回统一的错误响应给前端
 *
 * 使用示例：
 * throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS, "手机号已注册");
 * // 前端收到：{ "code": 1001, "message": "手机号已注册" }
 */
public class BusinessException extends RuntimeException {

    // 错误码，对应ErrorCode中定义的常量
    private Integer code;

    /**
     * 带错误码和错误信息的构造方法（推荐使用）
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 只有错误信息的构造方法，错误码默认500
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public Integer getCode() {
        return code;
    }
}
