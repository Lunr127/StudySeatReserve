package com.studyseat.reserve.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约查询数据传输对象
 */
@Data
public class ReservationQueryDTO {
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 座位ID
     */
    private Long seatId;
    
    /**
     * 自习室ID
     */
    private Long studyRoomId;
    
    /**
     * 预约日期（查询当天的所有预约）
     */
    private LocalDate date;
    
    /**
     * 开始时间范围（开始）
     */
    private LocalDateTime startTimeBegin;
    
    /**
     * 开始时间范围（结束）
     */
    private LocalDateTime startTimeEnd;
    
    /**
     * 状态：0-已取消，1-待签到，2-使用中，3-已完成，4-已违约
     */
    private Integer status;
} 