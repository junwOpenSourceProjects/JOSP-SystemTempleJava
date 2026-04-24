package com.josp.system.common.api;

import lombok.Data;

import java.io.Serializable;

/**
 * Generic result wrapper class for API responses in the JOSP System.
 * 
 * <p>This class provides a standard structure for all API responses containing:
 * <ul>
 *   <li>code - status code (success, error, validation failed, etc.)</li>
 *   <li>message - descriptive message</li>
 *   <li>data - generic payload data</li>
 * </ul>
 *
 * <p>Common usage:
 * <pre>
 * return Result.success(data);
 * return Result.failed("Validation error");
 * return Result.unauthorized(null);
 * </pre>
 *
 * @param <T> the type of the data payload
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class Result<T> implements Serializable {
    private long code;
    private String message;
    private T data;

    /**
     * Default constructor for JSON deserialization.
     */
    protected Result() {
    }

    /**
     * Constructs a Result with all fields.
     *
     * @param code the status code
     * @param message the descriptive message
     * @param data the payload data
     */
    protected Result(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * Creates a successful result with data.
     *
     * @param <T> the data type
     * @param data the payload data
     * @return Result with SUCCESS code and the provided data
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * Creates a successful result with data and custom message.
     *
     * @param <T> the data type
     * @param data the payload data
     * @param message custom success message
     * @return Result with SUCCESS code, custom message, and data
     */
    public static <T> Result<T> success(T data, String message) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * Creates a failed result with a message.
     *
     * @param <T> the data type
     * @param message error message
     * @return Result with ERROR code and the message
     */
    public static <T> Result<T> failed(String message) {
        return new Result<>(ResultCode.ERROR.getCode(), message, null);
    }

    /**
     * Creates a failed result with a ResultCode.
     *
     * @param <T> the data type
     * @param resultCode the ResultCode enum value
     * @return Result with the provided ResultCode's code and message
     */
    public static <T> Result<T> failed(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * Creates a validation failed result.
     *
     * @param <T> the data type
     * @param message validation error message
     * @return Result with VALIDATE_FAILED code and message
     */
    public static <T> Result<T> validateFailed(String message) {
        return new Result<>(ResultCode.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * Creates an unauthorized result.
     *
     * @param <T> the data type
     * @param data optional data payload
     * @return Result with UNAUTHORIZED code and message
     */
    public static <T> Result<T> unauthorized(T data) {
        return new Result<>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
    }

    /**
     * Creates a forbidden result.
     *
     * @param <T> the data type
     * @param data optional data payload
     * @return Result with FORBIDDEN code and message
     */
    public static <T> Result<T> forbidden(T data) {
        return new Result<>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data);
    }
}
