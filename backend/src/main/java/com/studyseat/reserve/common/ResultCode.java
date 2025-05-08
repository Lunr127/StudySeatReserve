package com.studyseat.reserve.common;

/**
 * 返回状态码
 */
public class ResultCode {
    
    /**
     * 成功
     */
    public static final int SUCCESS = 200;
    
    /**
     * 失败
     */
    public static final int ERROR = 500;
    
    /**
     * 参数错误
     */
    public static final int PARAM_ERROR = 400;
    
    /**
     * 未授权
     */
    public static final int UNAUTHORIZED = 401;
    
    /**
     * 禁止访问
     */
    public static final int FORBIDDEN = 403;
    
    /**
     * 资源不存在
     */
    public static final int NOT_FOUND = 404;
    
    /**
     * 业务异常
     */
    public static final int BUSINESS_ERROR = 501;
    
    /**
     * 数据库操作异常
     */
    public static final int DB_ERROR = 502;
    
    /**
     * 验证码错误
     */
    public static final int CAPTCHA_ERROR = 503;
    
    /**
     * 账号异常
     */
    public static final int ACCOUNT_ERROR = 504;
    
    /**
     * 系统限制
     */
    public static final int SYSTEM_LIMIT = 505;
    
    /**
     * 数据冲突
     */
    public static final int DATA_CONFLICT = 506;
} 