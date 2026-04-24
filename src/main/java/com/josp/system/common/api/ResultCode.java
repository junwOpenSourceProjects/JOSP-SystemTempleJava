package com.josp.system.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration of standardized result codes used across the JOSP System API.
 *
 * <p>These codes provide a consistent way for the frontend to determine
 * the outcome of an API call without parsing the message text.
 *
 * <p>Code ranges:
 * <ul>
 *   <li>200-299: Success codes</li>
 *   <li>400-499: Client error codes (validation, unauthorized, forbidden)</li>
 *   <li>500-599: Server error codes</li>
 *   <li>1000-1999: Business-specific codes (user auth, etc.)</li>
 * </ul>
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    /** Request succeeded */
    SUCCESS(200, "操作成功"),

    /** Internal server error (unexpected failure) */
    ERROR(500, "系统未知错误"),

    /** Client-side validation failed (e.g. invalid input parameters) */
    VALIDATE_FAILED(400, "参数检验失败"),

    /** Authentication required or token expired/invalid */
    UNAUTHORIZED(401, "暂未登录或Token已经过期"),

    /** Authenticated but lacks required permissions */
    FORBIDDEN(403, "没有相关权限"),

    /** User account does not exist in the database */
    USER_NOT_FOUND(1001, "账户不存在"),

    /** User exists but password does not match */
    USER_PASSWORD_ERROR(1002, "用户名或密码错误"),

    /** User account is locked/disabled (status = 0) */
    USER_ACCOUNT_LOCKED(1003, "账号已被锁定");

    private final long code;
    private final String message;
}
