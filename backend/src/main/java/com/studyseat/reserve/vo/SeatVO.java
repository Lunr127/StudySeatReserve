package com.studyseat.reserve.vo;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 座位视图对象
 */
@Data
public class SeatVO {
    
    /**
     * 座位ID
     */
    private Long id;
    
    /**
     * 所属自习室ID
     */
    private Long studyRoomId;
    
    /**
     * 所属自习室名称
     */
    private String studyRoomName;
    
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
     * 是否有电源（文字描述）
     */
    private String hasPowerText;
    
    /**
     * 是否靠窗（0-否，1-是）
     */
    private Integer isWindow;
    
    /**
     * 是否靠窗（文字描述）
     */
    private String isWindowText;
    
    /**
     * 是否角落（0-否，1-是）
     */
    private Integer isCorner;
    
    /**
     * 是否角落（文字描述）
     */
    private String isCornerText;
    
    /**
     * 状态（0-停用，1-正常）
     */
    private Integer status;
    
    /**
     * 状态（文字描述）
     */
    private String statusText;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 当前是否可用（是否有未完成的预约）
     */
    private Boolean isAvailable;
} 