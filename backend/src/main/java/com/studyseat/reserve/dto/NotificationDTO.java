package com.studyseat.reserve.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 通知消息数据传输对象
 */
@Data
@ApiModel(description = "通知消息数据传输对象")
public class NotificationDTO {
    
    @ApiModelProperty("接收用户ID")
    private Long userId;
    
    @ApiModelProperty("通知标题")
    private String title;
    
    @ApiModelProperty("通知内容")
    private String content;
    
    @ApiModelProperty("通知类型：1-系统通知，2-预约提醒，3-迟到提醒，4-违约通知")
    private Integer type;
} 