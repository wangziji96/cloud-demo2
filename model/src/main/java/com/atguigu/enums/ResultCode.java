package com.atguigu.enums;

import lombok.Getter;

@Getter
public enum ResultCode{
    SUCCESS(200, "操作成功"),
    ERROR(500, "系统错误"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "方法不允许"),
    LIMIT_REQUEST(429, "请求过于频繁");

    private final int code;
    private final String message;
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
