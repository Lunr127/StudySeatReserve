package com.studyseat.reserve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.dto.NotificationDTO;
import com.studyseat.reserve.entity.Notification;
import com.studyseat.reserve.entity.User;
import com.studyseat.reserve.mapper.NotificationMapper;
import com.studyseat.reserve.mapper.UserMapper;
import com.studyseat.reserve.service.NotificationService;
import com.studyseat.reserve.vo.NotificationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendNotification(NotificationDTO notificationDTO) {
        try {
            Notification notification = convertToEntity(notificationDTO);
            int result = notificationMapper.insert(notification);
            
            log.info("发送通知，用户ID: {}, 标题: {}, 结果: {}", 
                    notificationDTO.getUserId(), notificationDTO.getTitle(), result > 0 ? "成功" : "失败");
            
            return result > 0;
        } catch (Exception e) {
            log.error("发送通知失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSendNotifications(List<NotificationDTO> notifications) {
        try {
            if (notifications == null || notifications.isEmpty()) {
                return true;
            }
            
            List<Notification> notificationEntities = notifications.stream()
                    .map(this::convertToEntity)
                    .collect(Collectors.toList());
            
            int result = notificationMapper.batchInsert(notificationEntities);
            
            log.info("批量发送通知，数量: {}, 结果: {}", notifications.size(), result > 0 ? "成功" : "失败");
            
            return result > 0;
        } catch (Exception e) {
            log.error("批量发送通知失败", e);
            return false;
        }
    }
    
    @Override
    public IPage<NotificationVO> getUserNotifications(Page<NotificationVO> page, Long userId, Integer type, Integer isRead) {
        return notificationMapper.selectNotificationsByUserId(page, userId, type, isRead);
    }
    
    @Override
    public int getUnreadCount(Long userId) {
        return notificationMapper.countUnreadByUserId(userId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRead(Long notificationId, Long userId) {
        try {
            LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Notification::getId, notificationId)
                    .eq(Notification::getUserId, userId)
                    .eq(Notification::getIsRead, 0)
                    .set(Notification::getIsRead, 1)
                    .set(Notification::getUpdateTime, LocalDateTime.now());
            
            int result = notificationMapper.update(null, updateWrapper);
            
            log.info("标记通知为已读，通知ID: {}, 用户ID: {}, 结果: {}", 
                    notificationId, userId, result > 0 ? "成功" : "失败");
            
            return result > 0;
        } catch (Exception e) {
            log.error("标记通知为已读失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAllAsRead(Long userId) {
        try {
            int result = notificationMapper.markAllAsRead(userId);
            
            log.info("标记所有通知为已读，用户ID: {}, 影响行数: {}", userId, result);
            
            return result >= 0; // 即使没有未读通知，也返回成功
        } catch (Exception e) {
            log.error("标记所有通知为已读失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNotification(Long notificationId, Long userId) {
        try {
            LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Notification::getId, notificationId)
                    .eq(Notification::getUserId, userId)
                    .set(Notification::getIsDeleted, 1)
                    .set(Notification::getUpdateTime, LocalDateTime.now());
            
            int result = notificationMapper.update(null, updateWrapper);
            
            log.info("删除通知，通知ID: {}, 用户ID: {}, 结果: {}", 
                    notificationId, userId, result > 0 ? "成功" : "失败");
            
            return result > 0;
        } catch (Exception e) {
            log.error("删除通知失败", e);
            return false;
        }
    }
    
    @Override
    public void sendReservationReminder(Long userId, Long reservationId, String studyRoomName, String seatNumber, String startTime) {
        NotificationDTO notification = new NotificationDTO();
        notification.setUserId(userId);
        notification.setTitle("预约提醒");
        notification.setContent(String.format("您在%s的座位%s的预约即将开始，开始时间：%s，请及时前往签到。", 
                studyRoomName, seatNumber, startTime));
        notification.setType(2); // 预约提醒
        
        sendNotification(notification);
        
        log.info("发送预约提醒通知，用户ID: {}, 预约ID: {}", userId, reservationId);
    }
    
    @Override
    public void sendLateReminder(Long userId, Long reservationId, String studyRoomName, String seatNumber) {
        NotificationDTO notification = new NotificationDTO();
        notification.setUserId(userId);
        notification.setTitle("迟到提醒");
        notification.setContent(String.format("您在%s的座位%s已超时未签到，请尽快前往签到，否则预约将被自动取消。", 
                studyRoomName, seatNumber));
        notification.setType(3); // 迟到提醒
        
        sendNotification(notification);
        
        log.info("发送迟到提醒通知，用户ID: {}, 预约ID: {}", userId, reservationId);
    }
    
    @Override
    public void sendViolationNotification(Long userId, Long reservationId, Integer violationType, String description) {
        String violationTypeText = getViolationTypeText(violationType);
        
        NotificationDTO notification = new NotificationDTO();
        notification.setUserId(userId);
        notification.setTitle("违约通知");
        notification.setContent(String.format("您的预约存在违约行为：%s。详情：%s", violationTypeText, description));
        notification.setType(4); // 违约通知
        
        sendNotification(notification);
        
        log.info("发送违约通知，用户ID: {}, 预约ID: {}, 违约类型: {}", userId, reservationId, violationTypeText);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendSystemNotification(List<Long> userIds, String title, String content) {
        List<NotificationDTO> notifications = new ArrayList<>();
        
        if (userIds == null || userIds.isEmpty()) {
            // 发送给所有用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(User::getId);
            List<User> users = userMapper.selectList(queryWrapper);
            
            for (User user : users) {
                NotificationDTO notification = new NotificationDTO();
                notification.setUserId(user.getId());
                notification.setTitle(title);
                notification.setContent(content);
                notification.setType(1); // 系统通知
                notifications.add(notification);
            }
        } else {
            // 发送给指定用户
            for (Long userId : userIds) {
                NotificationDTO notification = new NotificationDTO();
                notification.setUserId(userId);
                notification.setTitle(title);
                notification.setContent(content);
                notification.setType(1); // 系统通知
                notifications.add(notification);
            }
        }
        
        if (!notifications.isEmpty()) {
            batchSendNotifications(notifications);
        }
        
        log.info("发送系统通知，用户数量: {}, 标题: {}", notifications.size(), title);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanExpiredNotifications(int days) {
        try {
            int result = notificationMapper.deleteExpiredNotifications(days);
            
            log.info("清理过期通知，保留天数: {}, 清理数量: {}", days, result);
            
            return result;
        } catch (Exception e) {
            log.error("清理过期通知失败", e);
            return 0;
        }
    }
    
    /**
     * 将DTO转换为实体
     */
    private Notification convertToEntity(NotificationDTO dto) {
        Notification notification = new Notification();
        notification.setUserId(dto.getUserId());
        notification.setTitle(dto.getTitle());
        notification.setContent(dto.getContent());
        notification.setType(dto.getType());
        notification.setIsRead(0); // 默认未读
        notification.setIsDeleted(0); // 默认未删除
        notification.setCreateTime(LocalDateTime.now());
        notification.setUpdateTime(LocalDateTime.now());
        return notification;
    }
    
    /**
     * 获取违约类型文字描述
     */
    private String getViolationTypeText(Integer type) {
        if (type == null) {
            return "未知违约";
        }
        
        switch (type) {
            case 1:
                return "未签到";
            case 2:
                return "迟到";
            case 3:
                return "提前离开";
            default:
                return "未知违约";
        }
    }
} 