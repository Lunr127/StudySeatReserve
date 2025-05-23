package com.studyseat.reserve.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户偏好设置数据传输对象
 */
@Data
@ApiModel(description = "用户偏好设置数据传输对象")
public class UserPreferenceDTO {
    
    @ApiModelProperty("是否启用消息通知")
    private Boolean enableNotification;
    
    @ApiModelProperty("是否启用自动取消预约")
    private Boolean enableAutoCancel;
    
    @ApiModelProperty("自动取消时间（分钟）")
    private Integer autoCancelMinutes;
    
    @ApiModelProperty("其他偏好设置（JSON格式）")
    private String preferences;
} 