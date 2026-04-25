package com.josp.system.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.mockito.Mockito;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Test profile Redis stub — 内存实现，绕过真实的 Redis 连接。
 *
 * LoginController 只依赖 StringRedisTemplate 的两个操作：
 *   redisTemplate.opsForValue().set(key, value, expiration, TimeUnit)
 *   redisTemplate.delete(key)
 *
 * 本配置提供内存中的 Map-backed 实现，替代真实的 Redis 连接。
 */
@TestConfiguration
public class TestRedisConfig {

    // ============================================================
    // 内存 Map（线程安全），模拟 Redis KV 存储
    // ============================================================
    public static final Map<String, String> IN_MEMORY_STORE = new ConcurrentHashMap<>();
    public static final Map<String, Long> IN_MEMORY_TTL = new ConcurrentHashMap<>();

    // ============================================================
    // StringRedisTemplate — 提供 opsForValue().set / delete
    // ============================================================

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return Mockito.mock(RedisConnectionFactory.class);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    // ============================================================
    // RedisTemplate<Object, Object> — MonitorService 等可能用到
    // ============================================================

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        return template;
    }
}
