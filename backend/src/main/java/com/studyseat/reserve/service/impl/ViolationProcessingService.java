package com.studyseat.reserve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyseat.reserve.entity.Reservation;
import com.studyseat.reserve.entity.Violation;
import com.studyseat.reserve.mapper.ReservationMapper;
import com.studyseat.reserve.mapper.ViolationMapper;
import com.studyseat.reserve.service.CheckCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 违约自动处理服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ViolationProcessingService {
    
    private final ReservationMapper reservationMapper;
    private final ViolationMapper violationMapper;
    private final CheckCodeService checkCodeService;
    
    /**
     * 处理超时未签到的预约（每分钟执行一次）
     */
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void processOverdueReservations() {
        log.debug("开始处理超时未签到的预约");
        
        // 查找所有待签到状态且超时15分钟的预约
        LocalDateTime overdueTime = LocalDateTime.now().minusMinutes(15);
        
        LambdaQueryWrapper<Reservation> query = new LambdaQueryWrapper<>();
        query.eq(Reservation::getStatus, 1) // 待签到状态
             .lt(Reservation::getStartTime, overdueTime); // 开始时间超过15分钟
        
        List<Reservation> overdueReservations = reservationMapper.selectList(query);
        
        for (Reservation reservation : overdueReservations) {
            try {
                // 更新预约状态为已违约
                reservation.setStatus(4);
                reservation.setUpdateTime(LocalDateTime.now());
                reservationMapper.updateById(reservation);
                
                // 创建违约记录
                Violation violation = new Violation();
                violation.setStudentId(reservation.getStudentId());
                violation.setReservationId(reservation.getId());
                violation.setViolationType(1); // 未签到
                violation.setDescription("预约后未在规定时间内签到");
                violation.setCreateTime(LocalDateTime.now());
                violation.setUpdateTime(LocalDateTime.now());
                
                violationMapper.insert(violation);
                
                log.info("处理超时未签到预约，预约ID: {}, 学生ID: {}", 
                    reservation.getId(), reservation.getStudentId());
                
            } catch (Exception e) {
                log.error("处理超时预约失败，预约ID: {}", reservation.getId(), e);
            }
        }
        
        if (!overdueReservations.isEmpty()) {
            log.info("处理超时未签到预约完成，共处理 {} 条记录", overdueReservations.size());
        }
    }
    
    /**
     * 自动生成明日签到码（每天凌晨1点执行）
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateTomorrowCheckCodes() {
        log.info("开始生成明日签到码");
        
        try {
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            Integer count = checkCodeService.generateDailyCodesForAllRooms(tomorrow);
            
            log.info("生成明日签到码完成，日期: {}, 生成数量: {}", tomorrow, count);
        } catch (Exception e) {
            log.error("生成明日签到码失败", e);
        }
    }
    
    /**
     * 清理过期签到码（每天凌晨2点执行）
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredCheckCodes() {
        log.info("开始清理过期签到码");
        
        try {
            // 这里可以添加清理过期签到码的逻辑
            // 例如：删除7天前的签到码记录
            
            log.info("清理过期签到码完成");
        } catch (Exception e) {
            log.error("清理过期签到码失败", e);
        }
    }
} 