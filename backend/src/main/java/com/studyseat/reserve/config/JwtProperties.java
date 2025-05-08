package com.studyseat.reserve.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.jwt")
public class JwtProperties {
    
    /**
     * JWT存储的请求头
     */
    private String tokenHeader;
    
    /**
     * JWT加解密使用的密钥
     */
    private String secret;
    
    /**
     * JWT的超期限时间(60*60*24*7)
     */
    private Long expiration;
    
    /**
     * JWT负载中拿到开头
     */
    private String tokenHead;
} 