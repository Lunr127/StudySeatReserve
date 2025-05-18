package com.studyseat.reserve.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.studyseat.reserve.dto.StudyRoomDTO;
import com.studyseat.reserve.entity.StudyRoom;
import com.studyseat.reserve.vo.StudyRoomVO;

/**
 * 自习室服务接口
 */
public interface StudyRoomService extends IService<StudyRoom> {
    
    /**
     * 创建自习室
     * 
     * @param studyRoomDTO 自习室DTO
     * @return 创建的自习室ID
     */
    Long createStudyRoom(StudyRoomDTO studyRoomDTO);
    
    /**
     * 更新自习室
     * 
     * @param studyRoomDTO 自习室DTO
     * @return 是否成功
     */
    boolean updateStudyRoom(StudyRoomDTO studyRoomDTO);
    
    /**
     * 删除自习室
     * 
     * @param id 自习室ID
     * @return 是否成功
     */
    boolean deleteStudyRoom(Long id);
    
    /**
     * 获取自习室详情
     * 
     * @param id 自习室ID
     * @return 自习室VO
     */
    StudyRoomVO getStudyRoomById(Long id);
    
    /**
     * 分页查询自习室
     * 
     * @param current 当前页
     * @param size 每页大小
     * @param name 自习室名称（模糊搜索）
     * @param building 所在建筑
     * @param belongsTo 归属（全校/特定院系）
     * @param isActive 是否开放
     * @return 分页结果
     */
    IPage<StudyRoomVO> pageStudyRoom(long current, long size, String name, 
                                   String building, String belongsTo, Integer isActive);
    
    /**
     * 更新自习室状态
     * 
     * @param id 自习室ID
     * @param isActive 是否开放（0-关闭，1-开放）
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer isActive);
    
    /**
     * 检查用户是否有权限访问自习室
     * 
     * @param studyRoomId 自习室ID
     * @param userId 用户ID
     * @return 是否有权限
     */
    boolean checkPermission(Long studyRoomId, Long userId);
} 