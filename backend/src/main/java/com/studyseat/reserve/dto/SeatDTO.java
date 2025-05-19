package com.studyseat.reserve.dto;

import lombok.Data;

/**
 * 座位数据传输对象
 */
@Data
public class SeatDTO {
    
    /**
     * 座位ID（新增时不需要传）
     */
    private Long id;
    
    /**
     * 所属自习室ID
     */
    private Long studyRoomId;
    
    /**
     * 座位编号
     */
    private String seatNumber;
    
    /**
     * 行号
     */
    private Integer rowNumber;
    
    /**
     * 列号
     */
    private Integer columnNumber;
    
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
} 