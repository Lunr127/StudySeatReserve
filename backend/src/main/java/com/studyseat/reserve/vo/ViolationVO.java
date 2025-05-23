package com.studyseat.reserve.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 违约记录视图对象
 */
@Data
@ApiModel(description = "违约记录视图对象")
public class ViolationVO {
    
    @ApiModelProperty("违约ID")
    private Long id;
    
    @ApiModelProperty("预约ID")
    private Long reservationId;
    
    @ApiModelProperty("自习室名称")
    private String studyRoomName;
    
    @ApiModelProperty("座位编号")
    private String seatNumber;
    
    @ApiModelProperty("违约类型：1-未签到，2-迟到，3-提前离开")
    private Integer violationType;
    
    @ApiModelProperty("违约类型文字描述")
    private String violationTypeText;
    
    @ApiModelProperty("预约时间")
    private String reservationTime;
    
    @ApiModelProperty("违约描述")
    private String description;
    
    @ApiModelProperty("违约时间")
    private LocalDateTime createTime;
} 