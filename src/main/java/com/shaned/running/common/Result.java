package com.shaned.running.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应格式，所有接口都返回这个结构：
 * {
 *   "code": 200,
 *   "message": "登录成功",
 *   "data": { ... },
 *   "timestamp": 1742294400000
 * }
 *
 * 用法：
 *   Result.success(data)              查询接口，message 默认 "success"
 *   Result.success("注册成功", data)  操作接口，自定义 message
 *   Result.error(code, message)       失败响应，data 为 null
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data, System.currentTimeMillis());
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data, System.currentTimeMillis());
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null, System.currentTimeMillis());
    }
}
