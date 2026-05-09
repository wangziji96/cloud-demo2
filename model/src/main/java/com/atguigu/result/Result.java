package com.atguigu.result;

import com.atguigu.enums.ResultCode;

import java.io.Serializable;

/**
 * 统一API响应结果封装类
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    public Result() {
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public Result(ResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    /**
     * 成功返回
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS);
    }

    /**
     * 成功返回，带数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS, data);
    }

    /**
     * 成功返回，带自定义消息和数据
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回
     */
    public static <T> Result<T> error() {
        return new Result<>(ResultCode.ERROR);
    }

    /**
     * 失败返回，带错误信息
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCode.ERROR.getCode(), message);
    }

    /**
     * 使用枚举错误码返回
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode);
    }

    /**
     * 自定义错误码和错误信息
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message);
    }

    /**
     * 自定义错误码、错误信息和数据
     */
    public static <T> Result<T> error(int code, String message, T data) {
        return new Result<>(code, message, data);
    }

    /**
     * 使用枚举错误码返回，带数据
     */
    public static <T> Result<T> error(ResultCode resultCode, T data) {
        return new Result<>(resultCode, data);
    }

    // getter和setter方法
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}