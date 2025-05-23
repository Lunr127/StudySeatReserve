package com.studyseat.reserve.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyseat.reserve.entity.UserPreference;

/**
 * 用户偏好设置Mapper接口
 */
@Mapper
public interface UserPreferenceMapper extends BaseMapper<UserPreference> {
    
    /**
     * 根据用户ID查询偏好设置
     * @param userId 用户ID
     * @return 用户偏好设置
     */
    UserPreference selectByUserId(@Param("userId") Long userId);
} 