package com.josp.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS (跨域资源共享) 配置类。
 *
 * <p>为 JOSP System 配置跨域 HTTP 请求，允许 Vue3 前端（运行在不同源）向此后端服务器发起 API 请求。
 *
 * <p>主要配置：
 * <ul>
 *   <li>允许前端 localhost:3000 开发环境访问</li>
 *   <li>允许携带凭证（cookies, Authorization header 等）</li>
 *   <li>暴露 Authorization header 供前端读取 JWT token</li>
 *   <li>缓存预检（OPTIONS）响应 1 小时</li>
 * </ul>
 *
 * <p><strong>注意：</strong>当 {@code allowCredentials=true} 时，不能使用
 * {@code originPatterns("*")}（CORS 规范不允许）。必须指定具体源或使用动态源检查。
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Configuration
public class CorsConfig {

    /**
     * 创建 CorsFilter Bean，拦截所有请求并添加 CORS 响应头。
     *
     * @return 配置好的 CorsFilter 实例
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许前端开发环境 localhost:3000/3001 访问（Vue3 默认端口）
        // 生产环境应替换为实际域名，如 "https://yourdomain.com"
        config.addAllowedOriginPattern("http://localhost:3000");
        config.addAllowedOriginPattern("http://127.0.0.1:3000");
        config.addAllowedOriginPattern("http://localhost:3001");
        config.addAllowedOriginPattern("http://127.0.0.1:3001");

        // 允许携带凭证（cookies、Authorization header 等）
        // 注意：设置了此选项后，origin 不能使用 "*"
        config.setAllowCredentials(true);

        // 允许所有请求头
        config.addAllowedHeader("*");

        // 允许所有 HTTP 方法
        config.addAllowedMethod("*");

        // 暴露 Authorization header，方便前端从响应中读取 JWT token
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Disposition");

        // 缓存预检（OPTIONS）响应 3600 秒（1 小时），减少重复预检请求
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
