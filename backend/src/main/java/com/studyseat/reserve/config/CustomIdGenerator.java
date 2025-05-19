package com.studyseat.reserve.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 自定义ID生成器，生成适合JavaScript安全整数范围内的ID
 * JavaScript的安全整数范围是 -(2^53-1) 到 2^53-1，即 -9007199254740991 到 9007199254740991
 */
@Component
public class CustomIdGenerator implements IdentifierGenerator {

    // 起始ID：让ID不要太小，看起来更真实
    private static final long START_ID = 100000;
    
    // 原子计数器，确保线程安全
    private final AtomicLong atomicLong = new AtomicLong(START_ID);

    @Override
    public Number nextId(Object entity) {
        // 生成下一个ID，确保在JavaScript安全整数范围内
        // 最大值9007199254740991，基本不会耗尽
        return atomicLong.incrementAndGet();
    }
} 