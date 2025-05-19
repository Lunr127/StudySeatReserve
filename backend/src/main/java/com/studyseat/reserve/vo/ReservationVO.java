package com.studyseat.reserve.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预约信息视图对象
 */
@Data
public class ReservationVO {
    
    /**
     * 预约ID
     */
    private Long id;
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 座位ID
     */
    private Long seatId;
    
    /**
     * 座位编号
     */
    private String seatNumber;
    
    /**
     * 自习室ID
     */
    private Long studyRoomId;
    
    /**
     * 自习室名称
     */
    private String studyRoomName;
    
    /**
     * 自习室位置
     */
    private String studyRoomLocation;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 状态：0-已取消，1-待签到，2-使用中，3-已完成，4-已违约
     */
    private Integer status;
    
    /**
     * 状态文字
     */
    private String statusText;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 是否可取消
     */
    private Boolean canCancel;
    
    /**
     * 是否可延长
     */
    private Boolean canExtend;
    
    /**
     * 是否可签到
     */
    private Boolean canCheckIn;
    
    /**
     * 是否已签到
     */
    private Boolean hasCheckedIn;
} 