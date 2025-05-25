package com.studyseat.reserve.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 签到数据传输对象
 */
@Data
public class CheckInDTO {
    
    /**
     * 预约ID
     */
    @NotNull(message = "预约ID不能为空")
    private Long reservationId;
    
    /**
     * 签到类型：1-扫码签到，2-手动输入编码
     */
    @NotNull(message = "签到类型不能为空")
    private Integer checkInType;
    
    /**
     * 签到码（手动输入编码时需要）
     */
    private String checkCode;
    
    /**
     * 自习室ID（用于验证签到码）
     */
    private Long studyRoomId;
} 