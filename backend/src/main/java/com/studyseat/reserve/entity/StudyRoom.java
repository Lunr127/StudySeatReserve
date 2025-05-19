package com.studyseat.reserve.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 自习室实体类
 */
@Data
@TableName("study_room")
public class StudyRoom {
    
    /**
     * 自习室ID
     */
    @TableId(type = IdType.AUTO)
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
     * 是否删除，0-未删除，1-已删除
     */
    @TableLogic
    private Integer isDeleted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 