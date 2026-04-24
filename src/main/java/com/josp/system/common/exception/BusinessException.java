package com.josp.system.common.exception;

import com.josp.system.common.api.ResultCode;
import lombok.Getter;

/**
 * Business exception class for the JOSP System.
 * 
 * <p>This exception is used to represent application-specific business logic errors
 * that can be handled and displayed to users. It contains both an error code and
 * error message for consistent error handling across the system.
 *
 * <p>Can be constructed with:
 * <ul>
 *   <li>Simple message string (uses ERROR code)</li>
 *   <li>Custom code and message</li>
 *   <li>ResultCode enum value</li>
 *   <li>ResultCode with custom message override</li>
 *   <li>With underlying cause exception</li>
 * </ul>
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final long code;
    private final String message;

    /**
     * Constructs a BusinessException with just a message.
     * Uses ResultCode.ERROR as the error code.
     *
     * @param message the error message
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.ERROR.getCode();
        this.message = message;
    }

    /**
     * Constructs a BusinessException with custom code and message.
     *
     * @param code the error code
     * @param message the error message
     */
    public BusinessException(long code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * Constructs a BusinessException with a ResultCode enum value.
     *
     * @param resultCode the ResultCode enum value
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    /**
     * Constructs a BusinessException with a ResultCode and custom message override.
     *
     * @param resultCode the ResultCode enum value
     * @param message custom message override
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }

    /**
     * Constructs a BusinessException with code, message and underlying cause.
     *
     * @param code the error code
     * @param message the error message
     * @param cause the underlying cause exception
     */
    public BusinessException(long code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    /**
     * Constructs a BusinessException with a ResultCode and underlying cause.
     *
     * @param resultCode the ResultCode enum value
     * @param cause the underlying cause exception
     */
    public BusinessException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
}
