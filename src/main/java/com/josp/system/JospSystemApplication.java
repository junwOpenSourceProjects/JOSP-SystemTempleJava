package com.josp.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * JOSP System — Spring Boot Application Entry Point.
 *
 * <p>Core technologies:
 * <ul>
 *   <li>Spring Boot 3.4.4</li>
 *   <li>Spring Security 6 (JWT-based stateless auth)</li>
 *   <li>MyBatis-Plus 3.5 (DAO layer)</li>
 *   <li>Redis (token blacklist, login logs)</li>
 *   <li>Redisson (distributed lock)</li>
 * </ul>
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@SpringBootApplication
@MapperScan("com.josp.system.dao")
public class JospSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(JospSystemApplication.class, args);
    }
}
