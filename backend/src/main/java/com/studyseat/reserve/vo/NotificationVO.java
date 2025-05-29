package com.studyseat.reserve.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知消息视图对象
 */
@Data
@ApiModel(description = "通知消息视图对象")
public class NotificationVO {
    
    @ApiModelProperty("通知ID")
    private Long id;
    
    @ApiModelProperty("接收用户ID")
    private Long userId;
    
    @ApiModelProperty("通知标题")
    private String title;
    
    @ApiModelProperty("通知内容")
    private String content;
    
    @ApiModelProperty("通知类型：1-系统通知，2-预约提醒，3-迟到提醒，4-违约通知")
    private Integer type;
    
    @ApiModelProperty("通知类型文字描述")
    private String typeText;
    
    @ApiModelProperty("是否已读：0-未读，1-已读")
    private Integer isRead;
    
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
    
    /**
     * 获取通知类型文字描述
     */
    public String getTypeText() {
        if (type == null) {
            return "未知";
        }
        
        switch (type) {
            case 1:
                return "系统通知";
            case 2:
                return "预约提醒";
            case 3:
                return "迟到提醒";
            case 4:
                return "违约通知";
            default:
                return "未知";
        }
    }
} 