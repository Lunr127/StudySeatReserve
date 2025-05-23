package com.studyseat.reserve.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户偏好设置实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_preference")
public class UserPreference {

    /**
     * 偏好设置ID，主键，自动递增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID，外键
     */
    private Long userId;

    /**
     * 是否启用消息通知
     */
    private Boolean enableNotification;

    /**
     * 是否启用自动取消预约
     */
    private Boolean enableAutoCancel;

    /**
     * 自动取消时间（分钟）
     */
    private Integer autoCancelMinutes;

    /**
     * 其他偏好设置（JSON格式）
     */
    private String preferences;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 