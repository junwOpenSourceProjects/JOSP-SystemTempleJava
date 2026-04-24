package com.josp.system.security.config;

import com.josp.system.security.jwt.JwtAuthenticationFilter;
import com.josp.system.security.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security configuration for the JOSP System.
 * Configures HTTP security, JWT authentication filter, and endpoint authorization.
 * 
 * <p>This configuration:
 * <ul>
 *   <li>Disables CSRF protection (stateless API)</li>
 *   <li>Configures stateless session management</li>
 *   <li>Sets up JWT authentication filter</li>
 *   <li>Defines public and protected endpoints</li>
 * </ul>
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Creates and configures the SecurityFilterChain for HTTP requests.
     * 
     * <p>This method:
     * <ul>
     *   <li>Disables CSRF for stateless API</li>
     *   <li>Configures CORS with default source</li>
     *   <li>Sets stateless session policy</li>
     *   <li>Configures endpoint authorization (permitAll for public endpoints)</li>
     *   <li>Adds JWT authentication filter before UsernamePasswordAuthenticationFilter</li>
     * </ul>
     *
     * @param http the HttpSecurity to configure
     * @param jwtTokenUtil the JWT token utility (lazy to avoid circular dependency)
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, @Lazy JwtTokenUtil jwtTokenUtil) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(new UrlBasedCorsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/auth/login",
                    "/api/v1/auth/register",
                    "/api/v1/auth/captcha",
                    "/api/v1/demo/**",
                    "/api/v1/dashboard/**",
                    "/api/v1/dict/**",
                    "/api/v1/dept/**",
                    "/api/v1/roles/**",
                    "/api/v1/menus/**",
                    "/api/v1/users/**",
                    "/api/v1/login-logs/**",
                    "/api/v1/oper-logs/**",
                    "/api/v1/notices/**",
                    "/api/v1/online-users/**",
                    "/api/v1/monitor/**",
                    "/doc.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/webjars/**"
                ).permitAll()
                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest().authenticated()
            );

        // Physical isolation: manually instantiate the filter to break circular dependency during Spring Bean initialization.
        // This filter is NOT managed by Spring Bean lifecycle, completely breaking the initialization loop.
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                applicationContext,
                jwtTokenUtil,
                tokenHeader,
                tokenHead
        );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
