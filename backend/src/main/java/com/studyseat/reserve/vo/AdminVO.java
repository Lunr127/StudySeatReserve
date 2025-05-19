package com.studyseat.reserve.vo;

import lombok.Data;

/**
 * 管理员视图对象
 */
@Data
public class AdminVO {
    
    /**
     * 管理员ID
     */
    private Long id;
    
    /**
     * 关联用户ID
     */
    private Long userId;
    
    /**
     * 管理员姓名
     */
    private String realName;
    
    /**
     * 管理员类型：1-系统管理员，2-自习室管理员
     */
    private Integer adminType;
    
    /**
     * 所属部门
     */
    private String department;
} 