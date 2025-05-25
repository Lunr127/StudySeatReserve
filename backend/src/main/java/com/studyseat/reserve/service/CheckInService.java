package com.studyseat.reserve.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.dto.CheckInDTO;
import com.studyseat.reserve.vo.CheckInVO;

/**
 * 签到服务接口
 */
public interface CheckInService {
    
    /**
     * 签到
     * 
     * @param checkInDTO 签到数据
     * @return 签到记录ID
     */
    Long checkIn(CheckInDTO checkInDTO);
    
    /**
     * 签退
     * 
     * @param reservationId 预约ID
     * @param studentId 学生ID
     * @return 操作结果
     */
    Boolean checkOut(Long reservationId, Long studentId);
    
    /**
     * 分页查询签到记录
     * 
     * @param page 分页参数
     * @param studyRoomId 自习室ID（可选）
     * @param studentId 学生ID（可选）
     * @return 签到记录分页
     */
    IPage<CheckInVO> getCheckInPage(Page<CheckInVO> page, Long studyRoomId, Long studentId);
    
    /**
     * 根据预约ID查询签到信息
     * 
     * @param reservationId 预约ID
     * @return 签到信息
     */
    CheckInVO getCheckInByReservationId(Long reservationId);
    
    /**
     * 根据ID查询签到详情
     * 
     * @param id 签到ID
     * @return 签到详情
     */
    CheckInVO getCheckInById(Long id);
    
    /**
     * 验证签到权限
     * 
     * @param reservationId 预约ID
     * @param studentId 学生ID
     * @return 验证结果
     */
    Boolean validateCheckInPermission(Long reservationId, Long studentId);
} 