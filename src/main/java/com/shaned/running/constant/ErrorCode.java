package com.shaned.running.constant;

/**
 * 错误码常量类
 *
 * 作用：统一定义项目中所有的错误码和错误信息。
 * 好处：
 * 1. 避免在代码中写魔法数字（比如直接写 1001），不知道含义
 * 2. 统一管理，修改错误信息只需改这一个地方
 * 3. 前端和后端对照这个文档，知道每个错误码的含义
 *
 * 错误码规范：
 * - 400：请求参数错误
 * - 401：未认证（没有登录）
 * - 403：无权限（登录了但没有权限）
 * - 404：资源不存在
 * - 500：服务器内部错误
 * - 1001~1999：用户模块业务错误
 * - 2001~2999：内容模块业务错误（后续扩展）
 */
public class ErrorCode {

    // ==================== 通用错误码 ====================

    // 请求参数错误（格式不对、缺少必填项等）
    public static final Integer PARAM_ERROR = 400;
    public static final String PARAM_ERROR_MSG = "参数错误";

    // 未认证（没有携带Token或Token无效）
    public static final Integer UNAUTHORIZED = 401;
    public static final String UNAUTHORIZED_MSG = "未认证";

    // 无权限（Token有效但没有操作权限）
    public static final Integer FORBIDDEN = 403;
    public static final String FORBIDDEN_MSG = "无权限";

    // 资源不存在
    public static final Integer NOT_FOUND = 404;
    public static final String NOT_FOUND_MSG = "资源不存在";

    // 服务器内部错误
    public static final Integer BUSINESS_ERROR = 500;
    public static final String BUSINESS_ERROR_MSG = "业务异常";

    // ==================== 用户模块错误码（1001~1999）====================

    // 手机号已注册
    public static final Integer PHONE_ALREADY_EXISTS = 1001;
    public static final String PHONE_ALREADY_EXISTS_MSG = "手机号已注册";

    // 手机号不存在（登录时手机号未注册）
    public static final Integer PHONE_NOT_FOUND = 1002;
    public static final String PHONE_NOT_FOUND_MSG = "手机号不存在";

    // 密码错误
    public static final Integer PASSWORD_ERROR = 1003;
    public static final String PASSWORD_ERROR_MSG = "密码错误";

    // 手机号格式错误
    public static final Integer PHONE_FORMAT_ERROR = 1004;
    public static final String PHONE_FORMAT_ERROR_MSG = "手机号格式错误";

    // 密码长度不足
    public static final Integer PASSWORD_LENGTH_ERROR = 1005;
    public static final String PASSWORD_LENGTH_ERROR_MSG = "密码长度至少6位";
}
