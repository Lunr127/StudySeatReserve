package com.studyseat.reserve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyseat.reserve.dto.CheckInDTO;
import com.studyseat.reserve.entity.CheckIn;
import com.studyseat.reserve.entity.Reservation;
import com.studyseat.reserve.exception.BusinessException;
import com.studyseat.reserve.mapper.CheckInMapper;
import com.studyseat.reserve.mapper.ReservationMapper;
import com.studyseat.reserve.service.CheckCodeService;
import com.studyseat.reserve.service.CheckInService;
import com.studyseat.reserve.vo.CheckInVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 签到服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckInServiceImpl extends ServiceImpl<CheckInMapper, CheckIn> implements CheckInService {
    
    private final CheckInMapper checkInMapper;
    private final ReservationMapper reservationMapper;
    private final CheckCodeService checkCodeService;
    
    @Override
    @Transactional
    public Long checkIn(CheckInDTO checkInDTO) {
        // 1. 验证预约是否存在且状态正确
        Reservation reservation = reservationMapper.selectById(checkInDTO.getReservationId());
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }
        
        // 检查预约状态，只有待签到状态才能签到
        if (!reservation.getStatus().equals(1)) {
            throw new BusinessException("预约状态不正确，无法签到");
        }
        
        // 2. 检查是否已经签到过
        LambdaQueryWrapper<CheckIn> checkInQuery = new LambdaQueryWrapper<>();
        checkInQuery.eq(CheckIn::getReservationId, checkInDTO.getReservationId());
        CheckIn existingCheckIn = checkInMapper.selectOne(checkInQuery);
        if (existingCheckIn != null) {
            throw new BusinessException("该预约已经签到过了");
        }
        
        // 3. 验证签到时间（预约开始时间前30分钟至后30分钟内可以签到）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = reservation.getStartTime();
        LocalDateTime endTime = reservation.getEndTime();
        
        if (now.isBefore(startTime.minusMinutes(30))) {
            throw new BusinessException("签到时间过早，请在预约开始前30分钟内签到");
        }
        
        if (now.isAfter(startTime.plusMinutes(30))) {
            throw new BusinessException("签到时间过晚，请在预约开始后30分钟内签到");
        }
        
        // 4. 如果是手动输入编码签到，验证签到码
        if (checkInDTO.getCheckInType().equals(2)) {
            if (checkInDTO.getCheckCode() == null || checkInDTO.getCheckCode().trim().isEmpty()) {
                throw new BusinessException("请输入签到码");
            }
            
            if (checkInDTO.getStudyRoomId() == null) {
                throw new BusinessException("自习室ID不能为空");
            }
            
            boolean isValidCode = checkCodeService.validateCheckCode(
                checkInDTO.getCheckCode(), 
                checkInDTO.getStudyRoomId(), 
                LocalDate.now()
            );
            
            if (!isValidCode) {
                throw new BusinessException("签到码无效或已过期");
            }
        }
        
        // 5. 创建签到记录
        CheckIn checkIn = new CheckIn();
        checkIn.setReservationId(checkInDTO.getReservationId());
        checkIn.setCheckInTime(now);
        checkIn.setCheckInType(checkInDTO.getCheckInType());
        checkIn.setCheckCode(checkInDTO.getCheckCode());
        checkIn.setCreateTime(now);
        checkIn.setUpdateTime(now);
        
        checkInMapper.insert(checkIn);
        
        // 6. 更新预约状态为使用中
        reservation.setStatus(2);
        reservation.setUpdateTime(now);
        reservationMapper.updateById(reservation);
        
        log.info("用户签到成功，预约ID: {}, 签到类型: {}", checkInDTO.getReservationId(), checkInDTO.getCheckInType());
        
        return checkIn.getId();
    }
    
    @Override
    @Transactional
    public Boolean checkOut(Long reservationId, Long studentId) {
        // 1. 验证预约是否存在且属于该学生
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }
        
        if (!reservation.getStudentId().equals(studentId)) {
            throw new BusinessException("无权操作该预约");
        }
        
        // 2. 检查预约状态，只有使用中状态才能签退
        if (!reservation.getStatus().equals(2)) {
            throw new BusinessException("预约状态不正确，无法签退");
        }
        
        // 3. 查找签到记录
        LambdaQueryWrapper<CheckIn> checkInQuery = new LambdaQueryWrapper<>();
        checkInQuery.eq(CheckIn::getReservationId, reservationId);
        CheckIn checkIn = checkInMapper.selectOne(checkInQuery);
        
        if (checkIn == null) {
            throw new BusinessException("未找到签到记录");
        }
        
        if (checkIn.getCheckOutTime() != null) {
            throw new BusinessException("已经签退过了");
        }
        
        // 4. 更新签到记录的签退时间
        LocalDateTime now = LocalDateTime.now();
        checkIn.setCheckOutTime(now);
        checkIn.setUpdateTime(now);
        checkInMapper.updateById(checkIn);
        
        // 5. 更新预约状态为已完成
        reservation.setStatus(3);
        reservation.setUpdateTime(now);
        reservationMapper.updateById(reservation);
        
        log.info("用户签退成功，预约ID: {}", reservationId);
        
        return true;
    }
    
    @Override
    public IPage<CheckInVO> getCheckInPage(Page<CheckInVO> page, Long studyRoomId, Long studentId) {
        return checkInMapper.selectCheckInPage(page, studyRoomId, studentId);
    }
    
    @Override
    public CheckInVO getCheckInByReservationId(Long reservationId) {
        return checkInMapper.selectByReservationId(reservationId);
    }
    
    @Override
    public CheckInVO getCheckInById(Long id) {
        return checkInMapper.selectCheckInById(id);
    }
    
    @Override
    public Boolean validateCheckInPermission(Long reservationId, Long studentId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            return false;
        }
        
        // 检查预约是否属于该学生
        if (!reservation.getStudentId().equals(studentId)) {
            return false;
        }
        
        // 检查预约状态是否为待签到
        if (!reservation.getStatus().equals(1)) {
            return false;
        }
        
        // 检查是否在签到时间范围内
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = reservation.getStartTime();
        
        return !now.isBefore(startTime.minusMinutes(30)) && !now.isAfter(startTime.plusMinutes(30));
    }
} 