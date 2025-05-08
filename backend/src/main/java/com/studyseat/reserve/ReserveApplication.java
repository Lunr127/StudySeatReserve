package com.studyseat.reserve;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 自习座位预约系统启动类
 */
@SpringBootApplication
@MapperScan("com.studyseat.reserve.mapper")
@EnableScheduling
public class ReserveApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReserveApplication.class, args);
    }
}