package com.studyseat.reserve.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 违约记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("violation")
public class Violation {

    /**
     * 违约ID，主键，自动递增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID，外键
     */
    private Long studentId;

    /**
     * 预约ID，外键
     */
    private Long reservationId;

    /**
     * 违约类型：1-未签到，2-迟到，3-提前离开
     */
    private Integer violationType;

    /**
     * 违约描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 