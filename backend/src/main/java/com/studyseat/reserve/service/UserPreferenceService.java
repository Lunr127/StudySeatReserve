package com.studyseat.reserve.service;

import com.studyseat.reserve.dto.UserPreferenceDTO;
import com.studyseat.reserve.vo.UserPreferenceVO;

/**
 * 用户偏好设置服务接口
 */
public interface UserPreferenceService {
    
    /**
     * 获取用户偏好设置
     * 
     * @param userId 用户ID
     * @return 用户偏好设置
     */
    UserPreferenceVO getUserPreference(Long userId);
    
    /**
     * 更新用户偏好设置
     * 
     * @param userId 用户ID
     * @param preferenceDTO 偏好设置数据
     * @return 是否成功
     */
    boolean updateUserPreference(Long userId, UserPreferenceDTO preferenceDTO);
    
    /**
     * 创建默认用户偏好设置
     * 
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean createDefaultPreference(Long userId);
} 