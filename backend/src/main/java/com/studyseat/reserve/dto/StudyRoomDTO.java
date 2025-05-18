package com.studyseat.reserve.dto;

import java.time.LocalTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Data;

/**
 * 自习室数据传输对象
 */
@Data
public class StudyRoomDTO {
    
    /**
     * 自习室ID（更新时使用）
     */
    private Long id;
    
    /**
     * 自习室名称
     */
    @NotBlank(message = "自习室名称不能为空")
    private String name;
    
    /**
     * 位置描述
     */
    @NotBlank(message = "位置描述不能为空")
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
    @NotNull(message = "座位容量不能为空")
    @Min(value = 1, message = "座位容量最小为1")
    @Max(value = 1000, message = "座位容量最大为1000")
    private Integer capacity;
    
    /**
     * 详细描述
     */
    private String description;
    
    /**
     * 开放时间(开始)
     */
    @NotNull(message = "开放开始时间不能为空")
    private LocalTime openTime;
    
    /**
     * 开放时间(结束)
     */
    @NotNull(message = "开放结束时间不能为空")
    private LocalTime closeTime;
    
    /**
     * 归属(全校/特定院系)
     */
    private String belongsTo;
    
    /**
     * 是否开放，0-关闭，1-开放
     */
    @NotNull(message = "开放状态不能为空")
    @Min(value = 0, message = "开放状态只能为0或1")
    @Max(value = 1, message = "开放状态只能为0或1")
    private Integer isActive;
    
    /**
     * 管理员ID
     */
    private Long adminId;
} 