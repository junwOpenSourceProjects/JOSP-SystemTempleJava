package com.josp.system.common.aspect;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.josp.system.common.utils.IpUtils;
import com.josp.system.entity.LoginUser;
import com.josp.system.entity.OperLog;
import com.josp.system.service.OperLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperLogAspect {

    private final OperLogService operLogService;

    /**
     * 保存操作日志的线程变量
     */
    private static final ThreadLocal<OperLog> OPER_LOG_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 定义切点 - 使用完全限定名避免歧义
     */
    @Pointcut("@annotation(com.josp.system.common.annotation.OperLog)")
    public void operLogPointcut() {
    }

    /**
     * 前置通知
     */
    @Before("operLogPointcut()")
    public void beforeOperLog(JoinPoint joinPoint) {
        try {
            // 获取请求对象
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            HttpServletRequest request = attributes.getRequest();

            // 获取注解信息
            com.josp.system.common.annotation.OperLog operLogAnnotation = getOperLogAnnotation(joinPoint);
            if (operLogAnnotation == null) {
                return;
            }

            // 创建操作日志对象
            OperLog operLog = new OperLog();
            operLog.setTitle(operLogAnnotation.title());
            operLog.setBusinessType(operLogAnnotation.businessType());
            operLog.setOperatorType(operLogAnnotation.operatorType());
            operLog.setRequestMethod(request.getMethod());
            operLog.setMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
            operLog.setOperUrl(request.getRequestURI());
            operLog.setOperIp(IpUtils.getIpAddress(request));
            operLog.setOperLocation(IpUtils.getIpLocation(operLog.getOperIp()));
            operLog.setOperTime(LocalDateTime.now());

            // 获取当前登录用户信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof LoginUser loginUser) {
                    operLog.setOperId(loginUser.getId());
                    operLog.setOperName(loginUser.getUsername());
                }
            }

            // 保存请求参数
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                try {
                    String params = JSON.toJSONString(args);
                    // 截断过长的参数
                    if (params.length() > 2000) {
                        params = params.substring(0, 2000);
                    }
                    operLog.setOperParam(params);
                } catch (Exception e) {
                    log.warn("序列化请求参数失败", e);
                }
            }

            // 保存到线程变量
            OPER_LOG_THREAD_LOCAL.set(operLog);
        } catch (Exception e) {
            log.error("记录操作日志前置信息失败", e);
        }
    }

    /**
     * 返回通知
     */
    @AfterReturning(pointcut = "operLogPointcut()", returning = "result")
    public void afterOperLog(JoinPoint joinPoint, Object result) {
        try {
            OperLog operLog = OPER_LOG_THREAD_LOCAL.get();
            if (operLog == null) {
                return;
            }

            // 设置返回参数
            if (result != null) {
                try {
                    String jsonResult = JSON.toJSONString(result);
                    if (jsonResult.length() > 2000) {
                        jsonResult = jsonResult.substring(0, 2000);
                    }
                    operLog.setJsonResult(jsonResult);
                } catch (Exception e) {
                    log.warn("序列化返回参数失败", e);
                }
            }

            // 设置成功状态
            operLog.setStatus(1);
            operLog.setErrorMsg("");

            // 保存日志
            operLogService.save(operLog);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        } finally {
            OPER_LOG_THREAD_LOCAL.remove();
        }
    }

    /**
     * 异常通知
     */
    @AfterThrowing(pointcut = "operLogPointcut()", throwing = "exception")
    public void afterThrowingOperLog(JoinPoint joinPoint, Throwable exception) {
        try {
            OperLog operLog = OPER_LOG_THREAD_LOCAL.get();
            if (operLog == null) {
                return;
            }

            // 设置异常状态
            operLog.setStatus(0);
            operLog.setErrorMsg(exception.getMessage());

            // 保存日志
            operLogService.save(operLog);
        } catch (Exception e) {
            log.error("保存操作日志异常信息失败", e);
        } finally {
            OPER_LOG_THREAD_LOCAL.remove();
        }
    }

    /**
     * 获取OperLog注解
     */
    private com.josp.system.common.annotation.OperLog getOperLogAnnotation(JoinPoint joinPoint) {
        try {
            String methodName = joinPoint.getSignature().getName();
            Class<?> className = joinPoint.getTarget().getClass();
            Class<?>[] parameterTypes = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getParameterTypes();
            java.lang.reflect.Method method = className.getMethod(methodName, parameterTypes);
            if (method != null) {
                return method.getAnnotation(com.josp.system.common.annotation.OperLog.class);
            }
        } catch (Exception e) {
            log.warn("获取OperLog注解失败", e);
        }
        return null;
    }
}
