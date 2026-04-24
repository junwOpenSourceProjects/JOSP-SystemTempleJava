package com.josp.system.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Authentication-related basic configuration for the JOSP System.
 * This configuration is separated from SecurityConfig to avoid circular dependency.
 * 
 * <p>Provides:
 * <ul>
 *   <li>BCrypt password encoder bean for secure password hashing</li>
 * </ul>
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Configuration
public class AuthConfig {

    /**
     * Creates a BCrypt password encoder for secure password hashing.
     *
     * @return a new BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
