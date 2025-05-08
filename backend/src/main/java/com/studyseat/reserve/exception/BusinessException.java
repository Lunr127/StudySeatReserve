package com.studyseat.reserve.exception;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误码
     */
    private final Integer code;
    
    /**
     * 构造函数
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 501;
    }
    
    /**
     * 构造函数
     * @param message 错误消息
     * @param code 错误码
     */
    public BusinessException(String message, Integer code) {
        super(message);
        this.code = code;
    }
    
    /**
     * 构造函数
     * @param message 错误消息
     * @param cause 异常原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 501;
    }
    
    /**
     * 构造函数
     * @param message 错误消息
     * @param code 错误码
     * @param cause 异常原因
     */
    public BusinessException(String message, Integer code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    /**
     * 获取错误码
     * @return 错误码
     */
    public Integer getCode() {
        return code;
    }
} 