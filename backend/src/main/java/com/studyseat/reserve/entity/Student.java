package com.studyseat.reserve.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 学生实体类
 */
@Data
@TableName("student")
public class Student {
    
    /**
     * 学生ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关联用户ID
     */
    private Long userId;
    
    /**
     * 学号
     */
    private String studentId;
    
    /**
     * 学院
     */
    private String college;
    
    /**
     * 专业
     */
    private String major;
    
    /**
     * 年级
     */
    private String grade;
    
    /**
     * 班级
     */
    private String className;
    
    /**
     * 违约次数
     */
    private Integer violationCount;
    
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