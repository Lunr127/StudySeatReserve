package com.studyseat.reserve.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.common.Result;
import com.studyseat.reserve.dto.NotificationDTO;
import com.studyseat.reserve.service.NotificationService;
import com.studyseat.reserve.util.JwtUtil;
import com.studyseat.reserve.vo.NotificationVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 通知管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Api(tags = "通知管理")
public class NotificationController {
    
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;
    
    @GetMapping
    @ApiOperation("分页查询用户通知")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<IPage<NotificationVO>> getUserNotifications(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("通知类型") @RequestParam(required = false) Integer type,
            @ApiParam("是否已读") @RequestParam(required = false) Integer isRead,
            HttpServletRequest request) {
        
        try {
            Long userId = jwtUtil.getUserIdFromToken(request);
            
            Page<NotificationVO> page = new Page<>(current, size);
            IPage<NotificationVO> result = notificationService.getUserNotifications(page, userId, type, isRead);
            
            // 设置类型文字描述
            result.getRecords().forEach(notification -> {
                notification.setTypeText(notification.getTypeText());
            });
            
            return Result.ok(result);
        } catch (Exception e) {
            log.error("查询用户通知失败", e);
            return Result.error("查询通知失败");
        }
    }
    
    @GetMapping("/unread-count")
    @ApiOperation("获取用户未读通知数量")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Integer> getUnreadCount(HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(request);
            int count = notificationService.getUnreadCount(userId);
            return Result.ok(count);
        } catch (Exception e) {
            log.error("获取未读通知数量失败", e);
            return Result.error("获取未读通知数量失败");
        }
    }
    
    @PostMapping("/{id}/read")
    @ApiOperation("标记通知为已读")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Boolean> markAsRead(
            @ApiParam("通知ID") @PathVariable Long id,
            HttpServletRequest request) {
        
        try {
            Long userId = jwtUtil.getUserIdFromToken(request);
            boolean success = notificationService.markAsRead(id, userId);
            
            if (success) {
                return Result.ok(true);
            } else {
                return Result.error("标记通知为已读失败");
            }
        } catch (Exception e) {
            log.error("标记通知为已读失败", e);
            return Result.error("标记通知为已读失败");
        }
    }
    
    @PostMapping("/read-all")
    @ApiOperation("标记所有通知为已读")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Boolean> markAllAsRead(HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(request);
            boolean success = notificationService.markAllAsRead(userId);
            
            if (success) {
                return Result.ok(true);
            } else {
                return Result.error("标记所有通知为已读失败");
            }
        } catch (Exception e) {
            log.error("标记所有通知为已读失败", e);
            return Result.error("标记所有通知为已读失败");
        }
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation("删除通知")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Boolean> deleteNotification(
            @ApiParam("通知ID") @PathVariable Long id,
            HttpServletRequest request) {
        
        try {
            Long userId = jwtUtil.getUserIdFromToken(request);
            boolean success = notificationService.deleteNotification(id, userId);
            
            if (success) {
                return Result.ok(true);
            } else {
                return Result.error("删除通知失败");
            }
        } catch (Exception e) {
            log.error("删除通知失败", e);
            return Result.error("删除通知失败");
        }
    }
    
    @PostMapping("/system")
    @ApiOperation("发送系统通知")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> sendSystemNotification(
            @ApiParam("用户ID列表，为空则发送给所有用户") @RequestParam(required = false) List<Long> userIds,
            @ApiParam("通知标题") @RequestParam String title,
            @ApiParam("通知内容") @RequestParam String content) {
        
        try {
            notificationService.sendSystemNotification(userIds, title, content);
            return Result.ok(true);
        } catch (Exception e) {
            log.error("发送系统通知失败", e);
            return Result.error("发送系统通知失败");
        }
    }
    
    @PostMapping("/send")
    @ApiOperation("发送单个通知")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> sendNotification(@RequestBody NotificationDTO notificationDTO) {
        try {
            boolean success = notificationService.sendNotification(notificationDTO);
            
            if (success) {
                return Result.ok(true);
            } else {
                return Result.error("发送通知失败");
            }
        } catch (Exception e) {
            log.error("发送通知失败", e);
            return Result.error("发送通知失败");
        }
    }
    
    @PostMapping("/clean-expired")
    @ApiOperation("清理过期通知")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Integer> cleanExpiredNotifications(
            @ApiParam("保留天数") @RequestParam(defaultValue = "30") Integer days) {
        
        try {
            int count = notificationService.cleanExpiredNotifications(days);
            return Result.ok(count);
        } catch (Exception e) {
            log.error("清理过期通知失败", e);
            return Result.error("清理过期通知失败");
        }
    }
} 