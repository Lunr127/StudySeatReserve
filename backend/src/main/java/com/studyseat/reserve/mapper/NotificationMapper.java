package com.studyseat.reserve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.entity.Notification;
import com.studyseat.reserve.vo.NotificationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知消息Mapper接口
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    
    /**
     * 分页查询用户通知列表
     * @param page 分页参数
     * @param userId 用户ID
     * @param type 通知类型（可选）
     * @param isRead 是否已读（可选）
     * @return 通知列表
     */
    IPage<NotificationVO> selectNotificationsByUserId(Page<NotificationVO> page, 
                                                      @Param("userId") Long userId,
                                                      @Param("type") Integer type,
                                                      @Param("isRead") Integer isRead);
    
    /**
     * 查询用户未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    int countUnreadByUserId(@Param("userId") Long userId);
    
    /**
     * 标记用户的所有通知为已读
     * @param userId 用户ID
     * @return 影响行数
     */
    int markAllAsRead(@Param("userId") Long userId);
    
    /**
     * 批量插入通知
     * @param notifications 通知列表
     * @return 插入行数
     */
    int batchInsert(@Param("notifications") List<Notification> notifications);
    
    /**
     * 删除过期通知（软删除）
     * @param days 保留天数
     * @return 删除行数
     */
    int deleteExpiredNotifications(@Param("days") int days);
} 