package com.studyseat.reserve.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.entity.Violation;
import com.studyseat.reserve.vo.ViolationVO;

/**
 * 违约记录服务接口
 */
public interface ViolationService {
    
    /**
     * 创建违约记录
     * 
     * @param studentId 学生ID
     * @param reservationId 预约ID
     * @param violationType 违约类型：1-未签到，2-迟到，3-提前离开
     * @param description 违约描述
     * @return 违约记录ID
     */
    Long createViolation(Long studentId, Long reservationId, Integer violationType, String description);
    
    /**
     * 分页查询学生违约记录
     * 
     * @param studentId 学生ID
     * @param page 分页参数
     * @return 违约记录分页列表
     */
    IPage<ViolationVO> getViolationPageByStudentId(Long studentId, Page<ViolationVO> page);
    
    /**
     * 获取学生违约总数
     * 
     * @param studentId 学生ID
     * @return 违约总数
     */
    Integer getViolationCountByStudentId(Long studentId);
    
    /**
     * 根据ID查询违约记录详情
     * 
     * @param violationId 违约记录ID
     * @return 违约记录详情
     */
    ViolationVO getViolationById(Long violationId);
} 