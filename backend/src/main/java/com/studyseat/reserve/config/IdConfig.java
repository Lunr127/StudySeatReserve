package com.studyseat.reserve.config;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * ID生成器配置类
 * 确保使用AUTO模式生成ID
 */
@Configuration
public class IdConfig {

    /**
     * 配置默认的ID生成器
     * 覆盖MyBatis-Plus默认的雪花算法生成器
     *
     * @return IdentifierGenerator
     */
    @Bean
    @Primary
    public IdentifierGenerator idGenerator() {
        return new DefaultIdentifierGenerator();
    }
} 