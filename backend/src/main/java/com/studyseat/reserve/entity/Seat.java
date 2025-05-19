package com.studyseat.reserve.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 座位实体类
 */
@Data
@TableName("seat")
public class Seat {
    
    /**
     * 座位ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 所属自习室ID
     */
    @TableField("study_room_id")
    private Long studyRoomId;
    
    /**
     * 座位编号
     */
    @TableField("seat_number")
    private String seatNumber;
    
    /**
     * 行号
     */
    @TableField(value = "`row_number`")
    private Integer rowNumber;
    
    /**
     * 列号
     */
    @TableField(value = "`column_number`")
    private Integer columnNumber;
    
    /**
     * 是否有电源（0-无，1-有）
     */
    @TableField("has_power")
    private Integer hasPower;
    
    /**
     * 是否靠窗（0-否，1-是）
     */
    @TableField("is_window")
    private Integer isWindow;
    
    /**
     * 是否角落（0-否，1-是）
     */
    @TableField("is_corner")
    private Integer isCorner;
    
    /**
     * 状态（0-停用，1-正常）
     */
    @TableField("status")
    private Integer status;
    
    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
} 