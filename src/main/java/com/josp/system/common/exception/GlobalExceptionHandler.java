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
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public CommonResult<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常 [{}]: {}", request.getRequestURI(), e.getMessage());
        return CommonResult.failed(e.getCode(), e.getMessage());
    }

    /**
     * 处理系统未知异常
     */
    @ExceptionHandler(Exception.class)
    public CommonResult<String> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常 [{}]: ", request.getRequestURI(), e);
        return CommonResult.failed(ResultCode.ERROR);
    }

    /**
     * 处理访问权限异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public CommonResult<Void> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.warn("权限不足 [{}]: {}", request.getRequestURI(), e.getMessage());
        return CommonResult.forbidden(ResultCode.FORBIDDEN.getMessage());
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public CommonResult<Void> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        log.warn("认证失败 [{}]: {}", request.getRequestURI(), e.getMessage());
        return CommonResult.unauthorized(ResultCode.UNAUTHORIZED.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<Void> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldError() != null ?
            e.getBindingResult().getFieldError().getDefaultMessage() : "参数校验失败";
        log.warn("参数校验失败 [{}]: {}", request.getRequestURI(), message);
        return CommonResult.validateFailed(message);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public CommonResult<Void> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getFieldError() != null ?
            e.getFieldError().getDefaultMessage() : "参数绑定失败";
        log.warn("参数绑定失败 [{}]: {}", request.getRequestURI(), message);
        return CommonResult.validateFailed(message);
    }

    /**
     * 处理请求参数缺失异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public CommonResult<Void> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("请求参数缺失 [{}]: {}", request.getRequestURI(), e.getMessage());
        return CommonResult.validateFailed("请求参数 [" + e.getParameterName() + "] 不能为空");
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public CommonResult<Void> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("参数类型不匹配 [{}]: {}", request.getRequestURI(), e.getMessage());
        return CommonResult.validateFailed("参数 [" + e.getName() + "] 类型错误");
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public CommonResult<Void> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("资源不存在 [{}]: {}", request.getRequestURI(), e.getMessage());
        return CommonResult.failed(404, "资源不存在");
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public CommonResult<Void> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数 [{}]: {}", request.getRequestURI(), e.getMessage());
        return CommonResult.validateFailed(e.getMessage());
    }
}
