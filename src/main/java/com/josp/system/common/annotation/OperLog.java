package com.josp.system.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 标注在Controller方法上，自动记录操作日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperLog {

    /**
     * 操作标题
     */
    String title() default "";

    /**
     * 业务类型：0-其他，1-新增，2-修改，3-删除，4-查询，5-授权，6-导出，7-导入
     */
    String businessType() default "0";

    /**
     * 操作类别：0-用户端，1-管理端
     */
    String operatorType() default "1";
}
