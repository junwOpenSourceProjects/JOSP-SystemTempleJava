package com.josp.system.common.exception;

import com.josp.system.common.api.CommonResult;
import com.josp.system.common.api.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Global exception handler for the JOSP System.
 * Handles various exceptions thrown by controllers and services.
 * 
 * <p>This handler provides consistent error responses for:
 * <ul>
 *   <li>BusinessException - application-specific business logic errors</li>
 *   <li>AuthenticationException - login/credential errors</li>
 *   <li>AccessDeniedException - authorization failures</li>
 *   <li>Validation exceptions - method argument validation failures</li>
 *   <li>Binding exceptions - request parameter binding failures</li>
 *   <li>General system exceptions - unexpected errors</li>
 * </ul>
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles BusinessException thrown by application services.
     *
     * @param e the business exception
     * @param request the HTTP request for logging context
     * @return CommonResult with the business error code and message
     */
    @ExceptionHandler(BusinessException.class)
    public CommonResult<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("Business exception [{}]: {}", request.getRequestURI(), e.getMessage());
        return CommonResult.failed(e.getCode(), e.getMessage());
    }

    /**
     * Handles general Exception thrown by the system.
     *
     * @param e the exception
     * @param request the HTTP request for logging context
     * @return CommonResult with system error code
     */
    @ExceptionHandler(Exception.class)
    public CommonResult<String> handleException(Exception e, HttpServletRequest request) {
        log.error("System exception [{}]: ", request.getRequestURI(), e);
        return CommonResult.failed(ResultCode.ERROR);
    }

    /**
     * Handles Spring Security AccessDeniedException.
     *
     * @param e the access denied exception
     * @param request the HTTP request for logging context
     * @return CommonResult with forbidden status
     */
    @ExceptionHandler(AccessDeniedException.class)
    public CommonResult<Void> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.warn("Access denied [{}]: {}", request.getRequestURI(), e.getMessage());
        return CommonResult.forbidden(ResultCode.FORBIDDEN.getMessage());
    }

    /**
     * Handles Spring Security AuthenticationException (includes DisabledException).
     *
     * @param e the authentication exception
     * @param request the HTTP request for logging context
     * @return CommonResult with unauthorized status
     */
    @ExceptionHandler(AuthenticationException.class)
    public CommonResult<Void> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        log.warn("Authentication failed [{}]: {}", request.getRequestURI(), e.getMessage());
        return CommonResult.unauthorized(ResultCode.UNAUTHORIZED.getMessage());
    }

    /**
     * Handles @Valid validation exceptions from request body.
     *
     * @param e the method argument not valid exception
     * @param request the HTTP request for logging context
     * @return CommonResult with validation failed status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<Void> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldError() != null ?
            e.getBindingResult().getFieldError().getDefaultMessage() : "Validation failed";
        log.warn("Validation failed [{}]: {}", request.getRequestURI(), message);
        return CommonResult.validateFailed(message);
    }

    /**
     * Handles request parameter binding exceptions.
     *
     * @param e the bind exception
     * @param request the HTTP request for logging context
     * @return CommonResult with validation failed status
     */
    @ExceptionHandler(BindException.class)
    public CommonResult<Void> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getFieldError() != null ?
            e.getFieldError().getDefaultMessage() : "Parameter binding failed";
        log.warn("Binding failed [{}]: {}", request.getRequestURI(), message);
        return CommonResult.validateFailed(message);
    }

    /**
     * Handles missing required request parameter exceptions.
     *
     * @param e the missing servlet request parameter exception
     * @param request the HTTP request for logging context
     * @return CommonResult with validation failed status
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public CommonResult<Void> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("Missing parameter [{}]: {}", request.getRequestURI(), e.getMessage());
        return CommonResult.validateFailed("Required parameter [" + e.getParameterName() + "] cannot be empty");
    }

    /**
     * Handles type mismatch exceptions for method arguments.
     *
     * @param e the method argument type mismatch exception
     * @param request the HTTP request for logging context
     * @return CommonResult with validation failed status
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public CommonResult<Void> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("Type mismatch [{}]: {}", request.getRequestURI(), e.getMessage());
        return CommonResult.validateFailed("Parameter [" + e.getName() + "] has invalid type");
    }

    /**
     * Handles 404 NoHandlerFoundException.
     *
     * @param e the no handler found exception
     * @param request the HTTP request for logging context
     * @return CommonResult with 404 status
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public CommonResult<Void> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("Resource not found [{}]: {}", request.getRequestURI(), e.getMessage());
        return CommonResult.failed(404, "Resource not found");
    }

    /**
     * Handles IllegalArgumentException.
     *
     * @param e the illegal argument exception
     * @param request the HTTP request for logging context
     * @return CommonResult with validation failed status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public CommonResult<Void> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("Invalid argument [{}]: {}", request.getRequestURI(), e.getMessage());
        return CommonResult.validateFailed(e.getMessage());
    }
}
