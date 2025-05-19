package com.studyseat.reserve.dto;

import lombok.Data;

/**
 * 座位查询条件
 */
@Data
public class SeatQueryDTO {
    
    /**
     * 所属自习室ID
     */
    private Long studyRoomId;
    
    /**
     * 座位编号
     */
    private String seatNumber;
    
    /**
     * 是否有电源（0-无，1-有）
     */
    private Integer hasPower;
    
    /**
     * 是否靠窗（0-否，1-是）
     */
    private Integer isWindow;
    
    /**
     * 是否角落（0-否，1-是）
     */
    private Integer isCorner;
    
    /**
     * 状态（0-停用，1-正常）
     */
    private Integer status;
    
    /**
     * 当前是否可用（是否有未完成的预约）
     */
    private Boolean isAvailable;
} 