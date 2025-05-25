package com.studyseat.reserve.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 签到实体类
 */
@Data
@TableName("check_in")
public class CheckIn {
    
    /**
     * 签到ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 预约ID
     */
    private Long reservationId;
    
    /**
     * 签到时间
     */
    private LocalDateTime checkInTime;
    
    /**
     * 签到类型：1-扫码签到，2-手动输入编码
     */
    private Integer checkInType;
    
    /**
     * 签退时间（可为空，表示未签退）
     */
    private LocalDateTime checkOutTime;
    
    /**
     * 签到码
     */
    private String checkCode;
    
    /**
     * 是否删除：0-未删除，1-已删除
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