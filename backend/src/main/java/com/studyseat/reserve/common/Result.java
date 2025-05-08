package com.studyseat.reserve.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回结果
 */
@Data
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 成功标志
     */
    private boolean success;

    /**
     * 返回代码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 私有构造函数，不允许外部直接实例化
     */
    private Result() {
    }

    /**
     * 成功静态方法，无数据
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("操作成功");
        return result;
    }

    /**
     * 成功静态方法，带数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 成功静态方法，带消息和数据
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(ResultCode.SUCCESS);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败静态方法，带失败码和消息
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败静态方法，默认错误码
     */
    public static <T> Result<T> error(String message) {
        return error(ResultCode.ERROR, message);
    }

    /**
     * 参数错误
     */
    public static <T> Result<T> paramError(String message) {
        return error(ResultCode.PARAM_ERROR, message);
    }

    /**
     * 未授权或登录过期
     */
    public static <T> Result<T> unauthorized(String message) {
        return error(ResultCode.UNAUTHORIZED, message);
    }

    /**
     * 无权限访问
     */
    public static <T> Result<T> forbidden(String message) {
        return error(ResultCode.FORBIDDEN, message);
    }
    
    /**
     * 自定义返回消息
     */
    public Result<T> message(String message) {
        this.setMessage(message);
        return this;
    }
} 