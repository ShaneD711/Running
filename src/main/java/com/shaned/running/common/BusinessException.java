package com.shaned.running.common;

/**
 * 业务异常：携带错误码和错误信息，由 GlobalExceptionHandler 统一捕获处理
 *
 * 用法：throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS, ErrorCode.PHONE_ALREADY_EXISTS_MSG)
 * 继承 RuntimeException（非受检异常），抛出时不需要在方法签名上声明 throws
 */
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public Integer getCode() {
        return code;
    }
}
