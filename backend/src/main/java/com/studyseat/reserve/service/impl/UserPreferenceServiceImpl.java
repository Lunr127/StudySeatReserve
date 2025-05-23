package com.studyseat.reserve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyseat.reserve.dto.UserPreferenceDTO;
import com.studyseat.reserve.entity.UserPreference;
import com.studyseat.reserve.exception.BusinessException;
import com.studyseat.reserve.mapper.UserPreferenceMapper;
import com.studyseat.reserve.service.UserPreferenceService;
import com.studyseat.reserve.vo.UserPreferenceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户偏好设置服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPreferenceServiceImpl implements UserPreferenceService {
    
    private final UserPreferenceMapper userPreferenceMapper;
    
    @Override
    public UserPreferenceVO getUserPreference(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        UserPreference preference = userPreferenceMapper.selectByUserId(userId);
        
        if (preference == null) {
            // 如果没有偏好设置，创建默认设置
            createDefaultPreference(userId);
            preference = userPreferenceMapper.selectByUserId(userId);
        }
        
        // 转换为VO对象
        UserPreferenceVO vo = new UserPreferenceVO();
        vo.setUserId(preference.getUserId());
        vo.setEnableNotification(preference.getEnableNotification());
        vo.setEnableAutoCancel(preference.getEnableAutoCancel());
        vo.setAutoCancelMinutes(preference.getAutoCancelMinutes());
        vo.setPreferences(preference.getPreferences());
        
        return vo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserPreference(Long userId, UserPreferenceDTO preferenceDTO) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        UserPreference preference = userPreferenceMapper.selectByUserId(userId);
        
        if (preference == null) {
            // 如果没有偏好设置，先创建默认设置
            createDefaultPreference(userId);
            preference = userPreferenceMapper.selectByUserId(userId);
        }
        
        // 更新偏好设置
        if (preferenceDTO.getEnableNotification() != null) {
            preference.setEnableNotification(preferenceDTO.getEnableNotification());
        }
        if (preferenceDTO.getEnableAutoCancel() != null) {
            preference.setEnableAutoCancel(preferenceDTO.getEnableAutoCancel());
        }
        if (preferenceDTO.getAutoCancelMinutes() != null) {
            preference.setAutoCancelMinutes(preferenceDTO.getAutoCancelMinutes());
        }
        if (preferenceDTO.getPreferences() != null) {
            preference.setPreferences(preferenceDTO.getPreferences());
        }
        
        preference.setUpdateTime(LocalDateTime.now());
        
        int result = userPreferenceMapper.updateById(preference);
        
        log.info("更新用户偏好设置，用户ID: {}, 结果: {}", userId, result > 0 ? "成功" : "失败");
        
        return result > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDefaultPreference(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        // 检查是否已存在偏好设置
        UserPreference existing = userPreferenceMapper.selectByUserId(userId);
        if (existing != null) {
            return true; // 已存在，直接返回成功
        }
        
        // 创建默认偏好设置
        UserPreference preference = new UserPreference();
        preference.setUserId(userId);
        preference.setEnableNotification(true); // 默认启用通知
        preference.setEnableAutoCancel(false); // 默认不启用自动取消
        preference.setAutoCancelMinutes(15); // 默认15分钟
        preference.setPreferences("{}"); // 默认空JSON
        preference.setCreateTime(LocalDateTime.now());
        preference.setUpdateTime(LocalDateTime.now());
        
        int result = userPreferenceMapper.insert(preference);
        
        log.info("创建默认用户偏好设置，用户ID: {}, 结果: {}", userId, result > 0 ? "成功" : "失败");
        
        return result > 0;
    }
} 