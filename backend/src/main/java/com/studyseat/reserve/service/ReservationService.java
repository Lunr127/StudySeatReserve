package com.studyseat.reserve.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.dto.ReservationDTO;
import com.studyseat.reserve.dto.ReservationQueryDTO;
import com.studyseat.reserve.entity.Reservation;
import com.studyseat.reserve.vo.ReservationVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约服务接口
 */
public interface ReservationService {
    
    /**
     * 创建预约
     * 
     * @param studentId 学生ID
     * @param reservationDTO 预约信息
     * @return 预约ID
     */
    Long createReservation(Long studentId, ReservationDTO reservationDTO);
    
    /**
     * 取消预约
     * 
     * @param reservationId 预约ID
     * @param studentId 学生ID
     * @return 是否成功
     */
    boolean cancelReservation(Long reservationId, Long studentId);
    
    /**
     * 延长预约时间
     * 
     * @param reservationId 预约ID
     * @param studentId 学生ID
     * @param endTime 新的结束时间
     * @return 是否成功
     */
    boolean extendReservation(Long reservationId, Long studentId, LocalDateTime endTime);
    
    /**
     * 分页查询预约列表
     * 
     * @param queryDTO 查询条件
     * @param page 分页参数
     * @return 预约分页列表
     */
    IPage<ReservationVO> getReservationPage(ReservationQueryDTO queryDTO, Page<ReservationVO> page);
    
    /**
     * 根据ID查询预约详情
     * 
     * @param reservationId 预约ID
     * @return 预约详情
     */
    ReservationVO getReservationById(Long reservationId);
    
    /**
     * 检查座位在指定时间段是否被预约
     * 
     * @param seatId 座位ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 是否可预约
     */
    boolean checkSeatAvailable(Long seatId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 检查学生在指定日期的预约次数是否超限
     * 
     * @param studentId 学生ID
     * @param date 预约日期的开始时间
     * @return 是否可预约
     */
    boolean checkDailyReservationLimit(Long studentId, LocalDateTime date);
    
    /**
     * 获取学生当前有效的预约列表
     * 
     * @param studentId 学生ID
     * @return 预约列表
     */
    List<ReservationVO> getCurrentReservations(Long studentId);
    
    /**
     * 更新超时未签到的预约为违约状态
     */
    void updateNoShowReservations();
    
    /**
     * 查询某个座位今天的预约情况
     * 
     * @param seatId 座位ID
     * @return 预约列表
     */
    List<ReservationVO> getTodayReservationsBySeat(Long seatId);
} 