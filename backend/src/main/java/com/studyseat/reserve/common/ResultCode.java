package com.studyseat.reserve.common;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {
    
    SUCCESS(200, "操作成功"),
    FAILURE(400, "操作失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "请求的资源不存在"),
    ERROR(500, "系统内部错误"),
    
    // 用户相关错误码 1000-1999
    USER_NOT_FOUND(1000, "用户不存在"),
    USERNAME_OR_PASSWORD_ERROR(1001, "用户名或密码错误"),
    ACCOUNT_DISABLED(1002, "账号已被禁用"),
    USER_ALREADY_EXISTS(1003, "用户已存在"),
    WX_LOGIN_ERROR(1004, "微信登录失败"),
    
    // 参数相关错误码 2000-2999
    PARAM_ERROR(2000, "参数错误"),
    PARAM_MISSING(2001, "参数缺失"),
    
    // 业务相关错误码 3000-3999
    OPERATION_FAILED(3000, "操作失败"),
    DATA_ALREADY_EXISTS(3001, "数据已存在"),
    DATA_NOT_EXISTS(3002, "数据不存在");
    
    private final Integer code;
    private final String message;
    
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
} 