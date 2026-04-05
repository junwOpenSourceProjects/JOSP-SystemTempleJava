package com.josp.system.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一处理所有Controller层抛出的异常,返回标准格式的错误信息
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ShowResult<?> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        return ShowResult.sendError("系统异常,请稍后重试: " + e.getMessage());
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ShowResult<?> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常: {}", e.getMessage(), e);
        return ShowResult.sendError("系统异常,请联系管理员");
    }

    /**
     * 处理参数非法异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ShowResult<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("参数非法异常: {}", e.getMessage(), e);
        return ShowResult.sendError("参数错误: " + e.getMessage());
    }

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ShowResult<?> handleException(Exception e) {
        log.error("未知异常: {}", e.getMessage(), e);
        return ShowResult.sendError("系统异常,请稍后重试");
    }
}
