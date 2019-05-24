package com.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author XINZE
 */
@SpringBootApplication
@MapperScan("com.demo.dao")
public class RecommandApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecommandApplication.class);
    }
}
