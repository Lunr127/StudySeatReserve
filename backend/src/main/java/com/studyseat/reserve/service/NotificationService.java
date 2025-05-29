package com.studyseat.reserve.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.dto.NotificationDTO;
import com.studyseat.reserve.entity.Notification;
import com.studyseat.reserve.vo.NotificationVO;

import java.util.List;

/**
 * 通知服务接口
 */
public interface NotificationService {
    
    /**
     * 发送通知
     * @param notificationDTO 通知数据
     * @return 是否成功
     */
    boolean sendNotification(NotificationDTO notificationDTO);
    
    /**
     * 批量发送通知
     * @param notifications 通知列表
     * @return 是否成功
     */
    boolean batchSendNotifications(List<NotificationDTO> notifications);
    
    /**
     * 分页查询用户通知
     * @param page 分页参数
     * @param userId 用户ID
     * @param type 通知类型（可选）
     * @param isRead 是否已读（可选）
     * @return 通知列表
     */
    IPage<NotificationVO> getUserNotifications(Page<NotificationVO> page, Long userId, Integer type, Integer isRead);
    
    /**
     * 获取用户未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    int getUnreadCount(Long userId);
    
    /**
     * 标记通知为已读
     * @param notificationId 通知ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAsRead(Long notificationId, Long userId);
    
    /**
     * 标记用户所有通知为已读
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAllAsRead(Long userId);
    
    /**
     * 删除通知
     * @param notificationId 通知ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteNotification(Long notificationId, Long userId);
    
    /**
     * 发送预约提醒通知
     * @param userId 用户ID
     * @param reservationId 预约ID
     * @param studyRoomName 自习室名称
     * @param seatNumber 座位号
     * @param startTime 开始时间
     */
    void sendReservationReminder(Long userId, Long reservationId, String studyRoomName, String seatNumber, String startTime);
    
    /**
     * 发送迟到提醒通知
     * @param userId 用户ID
     * @param reservationId 预约ID
     * @param studyRoomName 自习室名称
     * @param seatNumber 座位号
     */
    void sendLateReminder(Long userId, Long reservationId, String studyRoomName, String seatNumber);
    
    /**
     * 发送违约通知
     * @param userId 用户ID
     * @param reservationId 预约ID
     * @param violationType 违约类型
     * @param description 违约描述
     */
    void sendViolationNotification(Long userId, Long reservationId, Integer violationType, String description);
    
    /**
     * 发送系统通知
     * @param userIds 用户ID列表，为空则发送给所有用户
     * @param title 通知标题
     * @param content 通知内容
     */
    void sendSystemNotification(List<Long> userIds, String title, String content);
    
    /**
     * 清理过期通知
     * @param days 保留天数
     * @return 清理数量
     */
    int cleanExpiredNotifications(int days);
} 