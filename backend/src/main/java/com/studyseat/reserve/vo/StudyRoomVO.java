package com.studyseat.reserve.vo;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Data;

/**
 * 自习室视图对象
 */
@Data
public class StudyRoomVO {
    
    /**
     * 自习室ID
     */
    private Long id;
    
    /**
     * 自习室名称
     */
    private String name;
    
    /**
     * 位置描述
     */
    private String location;
    
    /**
     * 所在建筑
     */
    private String building;
    
    /**
     * 所在楼层
     */
    private String floor;
    
    /**
     * 房间号
     */
    private String roomNumber;
    
    /**
     * 座位容量
     */
    private Integer capacity;
    
    /**
     * 已使用座位数
     */
    private Integer usedCapacity;
    
    /**
     * 详细描述
     */
    private String description;
    
    /**
     * 开放时间(开始)
     */
    private LocalTime openTime;
    
    /**
     * 开放时间(结束)
     */
    private LocalTime closeTime;
    
    /**
     * 归属(全校/特定院系)
     */
    private String belongsTo;
    
    /**
     * 是否开放，0-关闭，1-开放
     */
    private Integer isActive;
    
    /**
     * 管理员ID
     */
    private Long adminId;
    
    /**
     * 管理员姓名
     */
    private String adminName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 