package com.josp.system.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis 配置类。
 *
 * <p>配置 Redis 连接、序列化和缓存管理：
 * <ul>
 *   <li>RedisTemplate - 提供 Redis 操作模板，支持 String 和 JSON 序列化</li>
 *   <li>RedisCacheManager - 管理缓存配置，默认 TTL 1小时，字典缓存 24 小时</li>
 * </ul>
 *
 * <p>连接配置来自 application.yml 中的 spring.redis.* 配置项：
 * <ul>
 *   <li>host: localhost</li>
 *   <li>port: 6379</li>
 *   <li>database: 0</li>
 *   <li>timeout: 10s</li>
 * </ul>
 *
 * <p><strong>序列化说明：</strong>使用 Jackson2JsonRedisSerializer 进行对象序列化，
 * 自动将 Java 对象转为 JSON 存储到 Redis，取出时自动反序列化。
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Configuration
@EnableCaching
@Profile("!test")
public class RedisConfig {

    /**
     * 创建 Jackson JSON 序列化器。
     * 配置类型自动识别，支持多态类型反序列化。
     *
     * @return 配置好的 Jackson2JsonRedisSerializer
     */
    private Jackson2JsonRedisSerializer<Object> createSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return new Jackson2JsonRedisSerializer<>(mapper, Object.class);
    }

    /**
     * 创建 RedisTemplate 实例，配置 String 和 JSON 混合序列化方式。
     *
     * <p>Key 使用 String 序列化，Value 使用 Jackson JSON 序列化。
     * 支持存储普通对象，自动序列化和反序列化。
     *
     * @param connectionFactory Redis 连接工厂，由 Spring Boot 自动配置
     * @return 配置好的 RedisTemplate 实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // JSON 序列化配置
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = createSerializer();

        // String 序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key 采用 String 的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash 的 key 也采用 String 的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value 序列化方式采用 Jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash 的 value 序列化方式采用 Jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 创建 RedisCacheManager 实例，配置缓存策略。
     *
     * <p>默认缓存配置：
     * <ul>
     *   <li>TTL: 1 小时</li>
     *   <li>Key 序列化: String</li>
     *   <li>Value 序列化: Jackson JSON</li>
     *   <li>禁止缓存 null 值</li>
     * </ul>
     *
     * <p>额外缓存配置：
     * <ul>
     *   <li>dict 缓存: TTL 24 小时</li>
     * </ul>
     *
     * @param connectionFactory Redis 连接工厂，由 Spring Boot 自动配置
     * @return 配置好的 RedisCacheManager 实例
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Jackson2JsonRedisSerializer<Object> serializer = createSerializer();

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)) // 默认缓存1小时
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .withCacheConfiguration("dict", RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofHours(24)) // 字典缓存24小时
                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer)))
                .build();
    }
}
