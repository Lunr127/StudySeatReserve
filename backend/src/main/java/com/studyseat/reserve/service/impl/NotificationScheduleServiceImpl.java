package com.studyseat.reserve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyseat.reserve.entity.Reservation;
import com.studyseat.reserve.entity.UserPreference;
import com.studyseat.reserve.mapper.ReservationMapper;
import com.studyseat.reserve.mapper.UserPreferenceMapper;
import com.studyseat.reserve.service.NotificationScheduleService;
import com.studyseat.reserve.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知定时任务服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationScheduleServiceImpl implements NotificationScheduleService {
    
    private final NotificationService notificationService;
    private final ReservationMapper reservationMapper;
    private final UserPreferenceMapper userPreferenceMapper;
    
    @Override
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void sendReservationReminders() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime reminderTime = now.plusMinutes(15); // 15分钟后开始的预约
            
            // 查询15分钟后开始的待签到预约
            LambdaQueryWrapper<Reservation> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Reservation::getStatus, 1) // 待签到
                    .between(Reservation::getStartTime, now, reminderTime);
            
            List<Reservation> reservations = reservationMapper.selectList(queryWrapper);
            
            for (Reservation reservation : reservations) {
                // 检查用户是否启用了通知
                if (isNotificationEnabled(reservation.getStudentId())) {
                    // 发送预约提醒
                    notificationService.sendReservationReminder(
                            reservation.getStudentId(),
                            reservation.getId(),
                            "自习室", // 这里应该查询实际的自习室名称
                            "座位" + reservation.getSeatId(), // 这里应该查询实际的座位号
                            reservation.getStartTime().toString()
                    );
                }
            }
            
            if (!reservations.isEmpty()) {
                log.info("发送预约提醒通知，预约数量: {}", reservations.size());
            }
        } catch (Exception e) {
            log.error("发送预约提醒通知失败", e);
        }
    }
    
    @Override
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void sendLateReminders() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lateTime = now.minusMinutes(10); // 10分钟前开始但未签到的预约
            
            // 查询超时未签到的预约
            LambdaQueryWrapper<Reservation> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Reservation::getStatus, 1) // 待签到
                    .lt(Reservation::getStartTime, lateTime);
            
            List<Reservation> reservations = reservationMapper.selectList(queryWrapper);
            
            for (Reservation reservation : reservations) {
                // 检查用户是否启用了通知
                if (isNotificationEnabled(reservation.getStudentId())) {
                    // 发送迟到提醒
                    notificationService.sendLateReminder(
                            reservation.getStudentId(),
                            reservation.getId(),
                            "自习室", // 这里应该查询实际的自习室名称
                            "座位" + reservation.getSeatId() // 这里应该查询实际的座位号
                    );
                }
            }
            
            if (!reservations.isEmpty()) {
                log.info("发送迟到提醒通知，预约数量: {}", reservations.size());
            }
        } catch (Exception e) {
            log.error("发送迟到提醒通知失败", e);
        }
    }
    
    @Override
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void cleanExpiredNotifications() {
        try {
            int cleanedCount = notificationService.cleanExpiredNotifications(30); // 清理30天前的通知
            log.info("清理过期通知完成，清理数量: {}", cleanedCount);
        } catch (Exception e) {
            log.error("清理过期通知失败", e);
        }
    }
    
    /**
     * 检查用户是否启用了通知
     */
    private boolean isNotificationEnabled(Long userId) {
        try {
            UserPreference preference = userPreferenceMapper.selectByUserId(userId);
            return preference == null || preference.getEnableNotification(); // 默认启用
        } catch (Exception e) {
            log.warn("检查用户通知设置失败，用户ID: {}", userId, e);
            return true; // 默认启用
        }
    }
} 