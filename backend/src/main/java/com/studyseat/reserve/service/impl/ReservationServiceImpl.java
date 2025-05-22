package com.studyseat.reserve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.dto.ReservationDTO;
import com.studyseat.reserve.dto.ReservationQueryDTO;
import com.studyseat.reserve.entity.Reservation;
import com.studyseat.reserve.entity.Seat;
import com.studyseat.reserve.entity.Student;
import com.studyseat.reserve.entity.StudyRoom;
import com.studyseat.reserve.exception.BusinessException;
import com.studyseat.reserve.mapper.ReservationMapper;
import com.studyseat.reserve.mapper.SeatMapper;
import com.studyseat.reserve.mapper.StudentMapper;
import com.studyseat.reserve.mapper.StudyRoomMapper;
import com.studyseat.reserve.service.ReservationService;
import com.studyseat.reserve.vo.ReservationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 预约服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    
    private final ReservationMapper reservationMapper;
    private final SeatMapper seatMapper;
    private final StudentMapper studentMapper;
    private final StudyRoomMapper studyRoomMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReservation(Long studentId, ReservationDTO reservationDTO) {
        // 参数校验
        if (reservationDTO.getStartTime().isAfter(reservationDTO.getEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
        
        if (reservationDTO.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("开始时间不能早于当前时间");
        }
        
        // 获取自习室信息
        Seat seat = seatMapper.selectById(reservationDTO.getSeatId());
        if (seat == null) {
            throw new BusinessException("座位不存在");
        }
        
        // 检查座位状态
        if (seat.getStatus() != 1) {
            throw new BusinessException("座位已停用，不可预约");
        }
        
        // 获取自习室信息
        StudyRoom studyRoom = studyRoomMapper.selectById(seat.getStudyRoomId());
        if (studyRoom == null) {
            throw new BusinessException("自习室不存在");
        }
        
        // 检查自习室状态
        if (studyRoom.getIsActive() != 1) {
            throw new BusinessException("自习室已关闭，不可预约");
        }
        
        // 检查预约时间是否在自习室开放时间内
        LocalTime openTime = studyRoom.getOpenTime();
        LocalTime closeTime = studyRoom.getCloseTime();
        
        LocalTime startTimeOfDay = reservationDTO.getStartTime().toLocalTime();
        LocalTime endTimeOfDay = reservationDTO.getEndTime().toLocalTime();
        
        if (startTimeOfDay.isBefore(openTime) || endTimeOfDay.isAfter(closeTime)) {
            throw new BusinessException("预约时间不在自习室开放时间内");
        }
        
        // 检查预约时长
        long hours = reservationDTO.getStartTime().until(reservationDTO.getEndTime(), ChronoUnit.HOURS);
        if (hours < 1) {
            throw new BusinessException("预约时长不能少于1小时");
        }
        // 已移除预约时长上限限制
        
        // 检查学生是否有权限预约该自习室
        Student student = studentMapper.selectById(studentId);
        // TODO: 根据自习室的归属检查学生是否有权限预约
        
        // 检查学生违约次数是否过多
        if (student.getViolationCount() >= 3) {
            throw new BusinessException("您的违约次数过多，暂时无法预约");
        }
        
        // 检查座位是否已被预约
        if (!checkSeatAvailable(reservationDTO.getSeatId(), reservationDTO.getStartTime(), reservationDTO.getEndTime())) {
            throw new BusinessException("该座位在所选时间段已被预约");
        }
        
        // 检查学生当日预约次数是否超限
        if (!checkDailyReservationLimit(studentId, reservationDTO.getStartTime())) {
            throw new BusinessException("您当日的预约次数已达上限");
        }
        
        // 创建预约记录
        Reservation reservation = new Reservation();
        reservation.setStudentId(studentId);
        reservation.setSeatId(reservationDTO.getSeatId());
        reservation.setStartTime(reservationDTO.getStartTime());
        reservation.setEndTime(reservationDTO.getEndTime());
        reservation.setStatus(1); // 待签到状态
        
        reservationMapper.insert(reservation);
        
        return reservation.getId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelReservation(Long reservationId, Long studentId) {
        // 获取预约信息
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }
        
        // 检查是否是当前学生的预约
        if (!reservation.getStudentId().equals(studentId)) {
            throw new BusinessException("无权取消该预约");
        }
        
        // 检查预约状态是否可取消
        if (reservation.getStatus() != 1 && reservation.getStatus() != 2) {
            throw new BusinessException("当前状态不可取消预约");
        }
        
        // 更新预约状态为已取消
        reservation.setStatus(0);
        reservation.setUpdateTime(LocalDateTime.now());
        
        return reservationMapper.updateById(reservation) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean extendReservation(Long reservationId, Long studentId, LocalDateTime endTime) {
        // 获取预约信息
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }
        
        // 检查是否是当前学生的预约
        if (!reservation.getStudentId().equals(studentId)) {
            throw new BusinessException("无权延长该预约");
        }
        
        // 检查预约状态是否可延长
        if (reservation.getStatus() != 2) {
            throw new BusinessException("只有使用中的预约才能延长时间");
        }
        
        // 获取座位信息
        Seat seat = seatMapper.selectById(reservation.getSeatId());
        if (seat == null) {
            throw new BusinessException("座位不存在");
        }
        
        // 获取自习室信息
        StudyRoom studyRoom = studyRoomMapper.selectById(seat.getStudyRoomId());
        if (studyRoom == null) {
            throw new BusinessException("自习室不存在");
        }
        
        // 检查延长后的结束时间是否在自习室开放时间内
        LocalTime closeTime = studyRoom.getCloseTime();
        LocalTime newEndTimeOfDay = endTime.toLocalTime();
        
        if (newEndTimeOfDay.isAfter(closeTime)) {
            throw new BusinessException("延长后的时间超出了自习室的开放时间");
        }
        
        // 检查延长后的预约时长
        long hours = reservation.getStartTime().until(endTime, ChronoUnit.HOURS);
        // 已移除预约延长总时长上限限制
        
        // 检查延长的时间段内座位是否已被预约
        if (!checkSeatAvailable(reservation.getSeatId(), reservation.getEndTime(), endTime)) {
            throw new BusinessException("延长时间段内座位已被预约");
        }
        
        // 更新预约结束时间
        reservation.setEndTime(endTime);
        reservation.setUpdateTime(LocalDateTime.now());
        
        return reservationMapper.updateById(reservation) > 0;
    }
    
    @Override
    public IPage<ReservationVO> getReservationPage(ReservationQueryDTO queryDTO, Page<ReservationVO> page) {
        // 解析查询条件
        LocalDateTime startAfter = null;
        LocalDateTime startBefore = null;
        
        if (queryDTO.getDate() != null) {
            startAfter = queryDTO.getDate().atStartOfDay();
            startBefore = queryDTO.getDate().plusDays(1).atStartOfDay();
        } else {
            startAfter = queryDTO.getStartTimeBegin();
            startBefore = queryDTO.getStartTimeEnd();
        }
        
        return reservationMapper.selectReservationPage(
                page,
                queryDTO.getStudentId(),
                queryDTO.getSeatId(),
                queryDTO.getStudyRoomId(),
                queryDTO.getStatus(),
                startAfter,
                startBefore
        );
    }
    
    @Override
    public ReservationVO getReservationById(Long reservationId) {
        return reservationMapper.selectReservationById(reservationId);
    }
    
    @Override
    public boolean checkSeatAvailable(Long seatId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Reservation> overlappingReservations = reservationMapper.selectOverlappingReservations(seatId, startTime, endTime);
        return overlappingReservations.isEmpty();
    }
    
    @Override
    public boolean checkDailyReservationLimit(Long studentId, LocalDateTime date) {
        // 获取当天的开始和结束时间
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().plusDays(1).atStartOfDay();
        
        // 查询当日有效预约数量
        Integer count = reservationMapper.countValidReservationsForDay(studentId, startOfDay, endOfDay);
        
        // 默认每日最大预约次数为2次
        return count == null || count < 2;
    }
    
    @Override
    public List<ReservationVO> getCurrentReservations(Long studentId) {
        // 构建查询条件
        ReservationQueryDTO queryDTO = new ReservationQueryDTO();
        queryDTO.setStudentId(studentId);
        
        // 只查询待签到和使用中的预约
        Page<ReservationVO> page = new Page<>(1, 10);
        
        // 获取当前和未来的预约
        LocalDateTime now = LocalDateTime.now();
        
        // 自定义查询
        LambdaQueryWrapper<Reservation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Reservation::getStudentId, studentId)
                .in(Reservation::getStatus, 1, 2)
                .ge(Reservation::getEndTime, now)
                .orderByAsc(Reservation::getStartTime);
        
        List<Reservation> reservations = reservationMapper.selectList(queryWrapper);
        
        // 转换为VO对象
        List<ReservationVO> result = new ArrayList<>();
        for (Reservation reservation : reservations) {
            ReservationVO vo = reservationMapper.selectReservationById(reservation.getId());
            if (vo != null) {
                result.add(vo);
            }
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNoShowReservations() {
        LocalDateTime now = LocalDateTime.now();
        // 默认超时时间为15分钟
        int timeoutMinutes = 15;
        int updatedCount = reservationMapper.updateNoShowReservations(now, timeoutMinutes);
        log.info("更新了 {} 条超时未签到的预约为违约状态", updatedCount);
        
        // TODO: 更新学生违约次数
    }
    
    @Override
    public List<ReservationVO> getTodayReservationsBySeat(Long seatId) {
        // 获取今天的开始和结束时间
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();
        
        // 构建查询条件
        ReservationQueryDTO queryDTO = new ReservationQueryDTO();
        queryDTO.setSeatId(seatId);
        queryDTO.setStartTimeBegin(startOfDay);
        queryDTO.setStartTimeEnd(endOfDay);
        
        // 查询今天该座位的所有预约
        Page<ReservationVO> page = new Page<>(1, 100); // 假设一天最多100个预约
        IPage<ReservationVO> reservationPage = this.getReservationPage(queryDTO, page);
        
        return reservationPage.getRecords();
    }
} 