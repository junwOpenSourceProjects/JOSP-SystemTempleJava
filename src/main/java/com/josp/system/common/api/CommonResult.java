package com.josp.system.common.api;

import lombok.Data;

import java.io.Serializable;

/**
 * Common API Result Wrapper.
 *
 * <p>Standardized response envelope used throughout the JOSP System backend.
 * Every controller method returns this wrapper to provide consistent JSON structure:
 * <ul>
 *   <li>code - numeric status code (200=success, 400=validation error, 401=unauthorized, 403=forbidden, 500=server error)</li>
 *   <li>message - human-readable description of the result</li>
 *   <li>data - the actual payload (can be null for void responses)</li>
 *   <li>timestamp - server timestamp when the response was generated (ms since epoch)</li>
 * </ul>
 *
 * <p>This class differs from {@link Result} by including a timestamp field.
 * Controllers should prefer one or the other for consistency within the same module.
 *
 * @param <T> the type of the data payload
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class CommonResult<T> implements Serializable {
    private long code;
    private String message;
    private T data;
    /** Server-side timestamp (milliseconds since epoch) when this response was generated */
    private long timestamp;

    /**
     * Default constructor. Automatically sets timestamp to current time.
     */
    protected CommonResult() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructs a CommonResult with all fields.
     *
     * @param code    the numeric status code
     * @param message the descriptive message
     * @param data    the payload data (may be null)
     */
    protected CommonResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Creates a successful result with data payload.
     *
     * @param <T>  the data type
     * @param data the payload to return
     * @return CommonResult with SUCCESS code and the provided data
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * Creates a successful result with data and custom message.
     *
     * @param <T>     the data type
     * @param data    the payload to return
     * @param message custom success message
     * @return CommonResult with SUCCESS code, custom message, and data
     */
    public static <T> CommonResult<T> success(T data, String message) {
        return new CommonResult<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * Creates a successful result with no payload (void response).
     *
     * @param <T> the data type (use Void or null)
     * @return CommonResult with SUCCESS code and null data
     */
    public static <T> CommonResult<T> success() {
        return new CommonResult<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * Creates a failed result with a custom error message.
     *
     * @param <T>     the data type
     * @param message the error description
     * @return CommonResult with ERROR code and the message
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<>(ResultCode.ERROR.getCode(), message, null);
    }

    /**
     * Creates a failed result using a predefined ResultCode enum value.
     *
     * @param <T>        the data type
     * @param resultCode the ResultCode enum value containing code and message
     * @return CommonResult with the provided ResultCode's code and message
     */
    public static <T> CommonResult<T> failed(ResultCode resultCode) {
        return new CommonResult<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * Creates a failed result with explicit code and message.
     *
     * @param <T>     the data type
     * @param code    numeric error code
     * @param message error description
     * @return CommonResult with the given code and message
     */
    public static <T> CommonResult<T> failed(long code, String message) {
        return new CommonResult<>(code, message, null);
    }

    /**
     * Creates a validation failure result (e.g. invalid input parameters).
     *
     * @param <T>     the data type
     * @param message validation error description
     * @return CommonResult with VALIDATE_FAILED code and message
     */
    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<>(ResultCode.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * Creates an unauthorized result with custom message.
     *
     * @param <T>     the data type
     * @param message the unauthorized description
     * @return CommonResult with UNAUTHORIZED code and message
     */
    public static <T> CommonResult<T> unauthorized(String message) {
        return new CommonResult<>(ResultCode.UNAUTHORIZED.getCode(), message, null);
    }

    /**
     * Creates an unauthorized result with data payload.
     *
     * @param <T>  the data type
     * @param data optional data payload (e.g. challenge info)
     * @return CommonResult with UNAUTHORIZED code, default message, and data
     */
    public static <T> CommonResult<T> unauthorized(T data) {
        return new CommonResult<>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
    }

    /**
     * Creates a forbidden result (authenticated but not authorized).
     *
     * @param <T>     the data type
     * @param message the forbidden description
     * @return CommonResult with FORBIDDEN code and message
     */
    public static <T> CommonResult<T> forbidden(String message) {
        return new CommonResult<>(ResultCode.FORBIDDEN.getCode(), message, null);
    }

    /**
     * Creates a forbidden result with data payload.
     *
     * @param <T>  the data type
     * @param data optional data payload
     * @return CommonResult with FORBIDDEN code, default message, and data
     */
    public static <T> CommonResult<T> forbidden(T data) {
        return new CommonResult<>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data);
    }

    /**
     * Creates a result with completely arbitrary code, message, and data.
     * Use this only when none of the standard factory methods fit.
     *
     * @param <T>     the data type
     * @param code    arbitrary numeric code
     * @param message arbitrary message
     * @param data    arbitrary data payload
     * @return CommonResult with the given values
     */
    public static <T> CommonResult<T> build(long code, String message, T data) {
        return new CommonResult<>(code, message, data);
    }

    /**
     * Checks whether this result represents a successful operation.
     *
     * @return true if code equals SUCCESS code, false otherwise
     */
    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS.getCode();
    }
}
