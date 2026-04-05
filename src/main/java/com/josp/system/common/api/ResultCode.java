package com.josp.system.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    ERROR(500, "系统未知错误"),
    VALIDATE_FAILED(400, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或Token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    USER_NOT_FOUND(1001, "账户不存在"),
    USER_PASSWORD_ERROR(1002, "用户名或密码错误"),
    USER_ACCOUNT_LOCKED(1003, "账号已被锁定");

    private final long code;
    private final String message;
}
