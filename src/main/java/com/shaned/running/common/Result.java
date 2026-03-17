package com.shaned.running.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应格式
 *
 * 作用：所有接口的返回值都用这个类包装，保证前端收到的数据格式一致。
 *
 * 返回格式示例：
 * {
 *   "code": 200,        // 状态码，200表示成功
 *   "message": "success", // 提示信息
 *   "data": {},         // 实际数据
 *   "timestamp": 123456 // 时间戳
 * }
 *
 * 泛型 <T> 表示data字段可以是任意类型，比如：
 * - Result<UserVO>    登录接口返回用户信息
 * - Result<LoginVO>   登录接口返回Token+用户信息
 * - Result<?>         不关心data类型时使用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    // 状态码：200成功，400参数错误，401未认证，403无权限，500系统错误
    private Integer code;
    // 提示信息：给前端展示的文字，比如"注册成功"、"手机号已注册"
    private String message;
    // 实际数据：接口返回的业务数据
    private T data;
    // 时间戳：接口响应的时间，方便排查问题
    private Long timestamp;

    /**
     * 成功响应（只有data，message默认为"success"）
     * 使用场景：查询接口，直接返回数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data, System.currentTimeMillis());
    }

    /**
     * 成功响应（自定义message和data）
     * 使用场景：注册、登录等操作，需要返回提示信息
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data, System.currentTimeMillis());
    }

    /**
     * 失败响应
     * 使用场景：业务异常、参数错误等
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null, System.currentTimeMillis());
    }
}
