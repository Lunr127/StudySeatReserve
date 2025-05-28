package com.studyseat.reserve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.entity.Reservation;
import com.studyseat.reserve.vo.ReservationVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约Mapper接口
 */
public interface ReservationMapper extends BaseMapper<Reservation> {
    
    /**
     * 分页查询预约列表（带详细信息）
     * 
     * @param page 分页参数
     * @param studentId 学生ID
     * @param seatId 座位ID
     * @param studyRoomId 自习室ID
     * @param status 预约状态列表
     * @param startAfter 开始时间（之后）
     * @param startBefore 开始时间（之前）
     * @return 预约视图对象分页列表
     */
    IPage<ReservationVO> selectReservationPage(
            Page<ReservationVO> page,
            @Param("studentId") Long studentId,
            @Param("seatId") Long seatId,
            @Param("studyRoomId") Long studyRoomId,
            @Param("status") List<Integer> status,
            @Param("startAfter") LocalDateTime startAfter,
            @Param("startBefore") LocalDateTime startBefore
    );
    
    /**
     * 根据ID查询预约详情
     * 
     * @param id 预约ID
     * @return 预约视图对象
     */
    ReservationVO selectReservationById(@Param("id") Long id);
    
    /**
     * 查询指定时间段内座位的预约情况
     * 
     * @param seatId 座位ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 预约列表
     */
    List<Reservation> selectOverlappingReservations(
            @Param("seatId") Long seatId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 查询用户在指定日期的有效预约数量（用于限制单日预约次数）
     * 
     * @param studentId 学生ID
     * @param startOfDay 日期开始时间
     * @param endOfDay 日期结束时间
     * @return 预约数量
     */
    Integer countValidReservationsForDay(
            @Param("studentId") Long studentId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
    
    /**
     * 更新超时未签到的预约为违约状态
     * 
     * @param currentTime 当前时间
     * @param timeoutMinutes 超时分钟数
     * @return 影响行数
     */
    int updateNoShowReservations(
            @Param("currentTime") LocalDateTime currentTime,
            @Param("timeoutMinutes") Integer timeoutMinutes
    );
} 