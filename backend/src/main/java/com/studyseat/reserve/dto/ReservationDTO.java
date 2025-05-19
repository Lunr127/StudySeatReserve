package com.studyseat.reserve.dto;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 预约数据传输对象
 */
@Data
public class ReservationDTO {
    
    /**
     * 座位ID
     */
    @NotNull(message = "座位ID不能为空")
    private Long seatId;
    
    /**
     * 自习室ID (冗余数据，用于前端快速获取)
     */
    @NotNull(message = "自习室ID不能为空")
    private Long studyRoomId;
    
    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @Future(message = "开始时间必须是未来时间")
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @Future(message = "结束时间必须是未来时间")
    private LocalDateTime endTime;
} 