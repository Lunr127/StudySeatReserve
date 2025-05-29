package com.studyseat.reserve.service;

/**
 * 通知定时任务服务接口
 */
public interface NotificationScheduleService {
    
    /**
     * 发送预约前提醒通知
     */
    void sendReservationReminders();
    
    /**
     * 发送迟到提醒通知
     */
    void sendLateReminders();
    
    /**
     * 清理过期通知
     */
    void cleanExpiredNotifications();
} 