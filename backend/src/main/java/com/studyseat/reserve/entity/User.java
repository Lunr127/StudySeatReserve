package com.studyseat.reserve.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User {
    
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码(加密存储)
     */
    private String password;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 微信OpenID
     */
    private String openId;
    
    /**
     * 用户类型，1-管理员，2-学生
     */
    private Integer userType;
    
    /**
     * 状态，0-禁用，1-正常
     */
    private Integer status;
    
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