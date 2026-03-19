package com.shaned.running.constant;

/**
 * 错误码常量，每个错误码对应一个 _MSG 常量，成对使用：
 * throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS, ErrorCode.PHONE_ALREADY_EXISTS_MSG)
 *
 * 分段规范：
 *   400/401/403/404/500 — 通用
 *   1001~1999           — 用户模块
 *   2001~2999           — 预留给后续模块
 */
public class ErrorCode {

    public static final Integer PARAM_ERROR = 400;
    public static final String  PARAM_ERROR_MSG = "参数错误";

    public static final Integer UNAUTHORIZED = 401;
    public static final String  UNAUTHORIZED_MSG = "未认证";

    public static final Integer FORBIDDEN = 403;
    public static final String  FORBIDDEN_MSG = "无权限";

    public static final Integer NOT_FOUND = 404;
    public static final String  NOT_FOUND_MSG = "资源不存在";

    public static final Integer BUSINESS_ERROR = 500;
    public static final String  BUSINESS_ERROR_MSG = "业务异常";

    // ========== 用户模块 1001~1999 ==========

    public static final Integer PHONE_ALREADY_EXISTS = 1001; // 注册：手机号已注册
    public static final String  PHONE_ALREADY_EXISTS_MSG = "手机号已注册";

    public static final Integer PHONE_NOT_FOUND = 1002; // 登录：手机号不存在
    public static final String  PHONE_NOT_FOUND_MSG = "手机号不存在";

    public static final Integer PASSWORD_ERROR = 1003; // 登录：密码错误
    public static final String  PASSWORD_ERROR_MSG = "密码错误";

    // 预留：@Pattern/@Size 校验失败目前由 GlobalExceptionHandler 统一返回 400，暂未使用
    public static final Integer PHONE_FORMAT_ERROR = 1004;
    public static final String  PHONE_FORMAT_ERROR_MSG = "手机号格式错误";

    public static final Integer PASSWORD_LENGTH_ERROR = 1005;
    public static final String  PASSWORD_LENGTH_ERROR_MSG = "密码长度至少6位";
}
