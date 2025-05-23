package com.studyseat.reserve.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用响应结果类
 */
@Data
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
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

    private Result() {
    }

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功结果
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功结果（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功结果（带消息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败结果
     */
    public static <T> Result<T> failure() {
        return new Result<>(ResultCode.FAILURE.getCode(), ResultCode.FAILURE.getMessage(), null);
    }

    /**
     * 失败结果（带消息）
     */
    public static <T> Result<T> failure(String message) {
        return new Result<>(ResultCode.FAILURE.getCode(), message, null);
    }

    /**
     * 失败结果（带状态码和消息）
     */
    public static <T> Result<T> failure(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败结果（带状态码、消息和数据）
     */
    public static <T> Result<T> failure(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }

    /**
     * 根据指定ResultCode返回结果
     */
    public static <T> Result<T> response(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 根据指定ResultCode返回结果（带数据）
     */
    public static <T> Result<T> response(ResultCode resultCode, T data) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), data);
    }

    /**
     * 通用错误响应
     */
    public static <T> Result<T> error(ResultCode resultCode, String message) {
        return new Result<>(resultCode.getCode(), message, null);
    }

    /**
     * 错误响应（只带消息）
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCode.FAILURE.getCode(), message, null);
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

    /**
     * 成功结果（R.java兼容方法）
     */
    public static <T> Result<T> ok() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功结果（带数据）（R.java兼容方法）
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功结果（带消息和数据）（R.java兼容方法）
     */
    public static <T> Result<T> ok(T data, String message) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败结果（R.java兼容方法）
     */
    public static <T> Result<T> fail() {
        return new Result<>(ResultCode.FAILURE.getCode(), ResultCode.FAILURE.getMessage(), null);
    }

    /**
     * 失败结果（带消息）（R.java兼容方法）
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(ResultCode.FAILURE.getCode(), message, null);
    }

    /**
     * 失败结果（带状态码和消息）（R.java兼容方法）
     */
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败结果（带状态码、消息和数据）（R.java兼容方法）
     */
    public static <T> Result<T> fail(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }
} 