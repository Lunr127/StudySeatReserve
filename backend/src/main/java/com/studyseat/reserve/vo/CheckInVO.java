package com.studyseat.reserve.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 签到视图对象
 */
@Data
public class CheckInVO {
    
    /**
     * 签到ID
     */
    private Long id;
    
    /**
     * 预约ID
     */
    private Long reservationId;
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 自习室名称
     */
    private String studyRoomName;
    
    /**
     * 座位编号
     */
    private String seatNumber;
    
    /**
     * 签到时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkInTime;
    
    /**
     * 签到类型：1-扫码签到，2-手动输入编码
     */
    private Integer checkInType;
    
    /**
     * 签到类型文字
     */
    private String checkInTypeText;
    
    /**
     * 签退时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkOutTime;
    
    /**
     * 签到码
     */
    private String checkCode;
    
    /**
     * 是否已签退
     */
    private Boolean isCheckedOut;
    
    /**
     * 使用时长（分钟）
     */
    private Long durationMinutes;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
} 