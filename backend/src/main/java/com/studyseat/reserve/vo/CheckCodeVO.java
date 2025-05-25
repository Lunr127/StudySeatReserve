package com.studyseat.reserve.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 签到码视图对象
 */
@Data
public class CheckCodeVO {
    
    /**
     * ID
     */
    private Long id;
    
    /**
     * 自习室ID
     */
    private Long studyRoomId;
    
    /**
     * 自习室名称
     */
    private String studyRoomName;
    
    /**
     * 签到码
     */
    private String code;
    
    /**
     * 有效日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate validDate;
    
    /**
     * 是否有效：0-无效，1-有效
     */
    private Integer isActive;
    
    /**
     * 状态文字
     */
    private String statusText;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
} 