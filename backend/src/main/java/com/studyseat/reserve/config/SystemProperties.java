package com.studyseat.reserve.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 系统配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.system")
public class SystemProperties {
    
    /**
     * 签到有效时间（分钟）
     */
    private Integer checkinTimeout;
    
    /**
     * 默认最大预约时长（小时）
     */
    private Integer maxReserveHours;
    
    /**
     * 是否启用违约记录
     */
    private Boolean enableViolation;
    
    /**
     * 允许提前预约的天数
     */
    private Integer advanceReserveDays;
} 