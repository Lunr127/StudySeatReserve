package com.studyseat.reserve.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户信息视图对象
 */
@Data
@ApiModel(description = "用户信息视图对象")
public class UserInfoVO {
    
    @ApiModelProperty("用户ID")
    private Long id;
    
    @ApiModelProperty("用户名")
    private String username;
    
    @ApiModelProperty("真实姓名")
    private String realName;
    
    @ApiModelProperty("用户类型，1-管理员，2-学生")
    private Integer userType;
    
    @ApiModelProperty("手机号")
    private String phone;
    
    @ApiModelProperty("邮箱")
    private String email;
    
    @ApiModelProperty("头像URL")
    private String avatar;
    
    @ApiModelProperty("状态，0-禁用，1-正常")
    private Integer status;
} 