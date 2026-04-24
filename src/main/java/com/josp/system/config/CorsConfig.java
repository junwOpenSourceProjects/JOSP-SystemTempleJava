package com.josp.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS (Cross-Origin Resource Sharing) Configuration.
 *
 * <p>Configures cross-origin HTTP requests for the JOSP System.
 * Allows the Vue3 frontend (running on a different origin) to make API requests
 * to this backend server with proper credential handling.
 *
 * <p>Key settings:
 * <ul>
 *   <li>Allow all origin patterns (configure specific origins in production)</li>
 *   <li>Allow credentials (cookies, authorization headers)</li>
 *   <li>Expose Authorization header so the frontend can read JWT tokens</li>
 *   <li>Cache preflight (OPTIONS) responses for 1 hour</li>
 * </ul>
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Configuration
public class CorsConfig {

    /**
     * Creates a CorsFilter bean that intercepts all requests and applies CORS headers.
     *
     * @return a configured CorsFilter instance
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow all origins (use specific patterns in production, e.g. "https://yourdomain.com")
        config.addAllowedOriginPattern("*");

        // Allow credentials (cookies, Authorization header, etc.)
        config.setAllowCredentials(true);

        // Allow all request headers
        config.addAllowedHeader("*");

        // Allow all HTTP methods
        config.addAllowedMethod("*");

        // Expose Authorization header to the client so it can read the JWT from response
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Disposition");

        // Cache preflight (OPTIONS) response for 3600 seconds (1 hour)
        // Reduces OPTIONS preflight requests for the same URL
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
