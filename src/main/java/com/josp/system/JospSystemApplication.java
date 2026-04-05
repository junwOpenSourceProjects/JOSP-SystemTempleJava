package com.josp.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.josp.system.dao")
public class JospSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(JospSystemApplication.class, args);
    }

}
