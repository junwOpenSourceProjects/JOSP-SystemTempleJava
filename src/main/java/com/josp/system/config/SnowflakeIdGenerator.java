package com.josp.system.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.stereotype.Component;

/**
 * 雪花ID生成器
 * 使用MyBatis-Plus的IdentifierGenerator接口
 * 生成19位long类型主键
 */
@Component
public class SnowflakeIdGenerator implements IdentifierGenerator {

    private final Snowflake snowflake = IdUtil.getSnowflake(1, 1);

    @Override
    public Long nextId(Object entity) {
        return snowflake.nextId();
    }
}
