package com.studyseat.reserve.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyseat.reserve.dto.StudyRoomDTO;
import com.studyseat.reserve.entity.Admin;
import com.studyseat.reserve.entity.StudyRoom;
import com.studyseat.reserve.entity.User;
import com.studyseat.reserve.exception.BusinessException;
import com.studyseat.reserve.mapper.AdminMapper;
import com.studyseat.reserve.mapper.StudyRoomMapper;
import com.studyseat.reserve.mapper.UserMapper;
import com.studyseat.reserve.service.StudyRoomService;
import com.studyseat.reserve.vo.StudyRoomVO;

import lombok.extern.slf4j.Slf4j;

/**
 * 自习室服务实现类
 */
@Slf4j
@Service
public class StudyRoomServiceImpl extends ServiceImpl<StudyRoomMapper, StudyRoom> implements StudyRoomService {

    @Autowired
    private StudyRoomMapper studyRoomMapper;
    
    @Autowired
    private AdminMapper adminMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStudyRoom(StudyRoomDTO studyRoomDTO) {
        // 检查管理员是否存在
        if (studyRoomDTO.getAdminId() != null) {
            Admin admin = adminMapper.selectById(studyRoomDTO.getAdminId());
            if (admin == null) {
                throw new BusinessException("管理员不存在");
            }
        }
        
        // 创建自习室对象
        StudyRoom studyRoom = new StudyRoom();
        BeanUtils.copyProperties(studyRoomDTO, studyRoom);
        
        // 保存到数据库
        this.save(studyRoom);
        log.info("创建自习室成功，ID：{}", studyRoom.getId());
        
        return studyRoom.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStudyRoom(StudyRoomDTO studyRoomDTO) {
        // 检查自习室是否存在
        StudyRoom existingRoom = this.getById(studyRoomDTO.getId());
        if (existingRoom == null) {
            throw new BusinessException("自习室不存在");
        }
        
        // 检查管理员是否存在
        if (studyRoomDTO.getAdminId() != null) {
            Admin admin = adminMapper.selectById(studyRoomDTO.getAdminId());
            if (admin == null) {
                throw new BusinessException("管理员不存在");
            }
        }
        
        // 更新自习室信息
        StudyRoom studyRoom = new StudyRoom();
        BeanUtils.copyProperties(studyRoomDTO, studyRoom);
        
        // 保存到数据库
        boolean result = this.updateById(studyRoom);
        if (result) {
            log.info("更新自习室成功，ID：{}", studyRoom.getId());
        } else {
            log.error("更新自习室失败，ID：{}", studyRoom.getId());
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStudyRoom(Long id) {
        // 检查自习室是否存在
        StudyRoom existingRoom = this.getById(id);
        if (existingRoom == null) {
            throw new BusinessException("自习室不存在");
        }
        
        // 逻辑删除自习室
        boolean result = this.removeById(id);
        if (result) {
            log.info("删除自习室成功，ID：{}", id);
        } else {
            log.error("删除自习室失败，ID：{}", id);
        }
        
        return result;
    }

    @Override
    public StudyRoomVO getStudyRoomById(Long id) {
        // 获取自习室详情（包含管理员姓名和已使用座位数）
        StudyRoomVO studyRoomVO = studyRoomMapper.selectStudyRoomById(id);
        if (studyRoomVO == null) {
            throw new BusinessException("自习室不存在");
        }
        
        return studyRoomVO;
    }

    @Override
    public IPage<StudyRoomVO> pageStudyRoom(long current, long size, String name, String building, String belongsTo,
            Integer isActive) {
        // 创建分页对象
        Page<StudyRoomVO> page = new Page<>(current, size);
        
        // 查询分页数据
        IPage<StudyRoomVO> pageResult = studyRoomMapper.selectStudyRoomPage(page, name, building, belongsTo, isActive);
        
        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer isActive) {
        // 检查自习室是否存在
        StudyRoom existingRoom = this.getById(id);
        if (existingRoom == null) {
            throw new BusinessException("自习室不存在");
        }
        
        // 更新状态
        StudyRoom studyRoom = new StudyRoom();
        studyRoom.setId(id);
        studyRoom.setIsActive(isActive);
        
        // 保存到数据库
        boolean result = this.updateById(studyRoom);
        if (result) {
            log.info("更新自习室状态成功，ID：{}，状态：{}", id, isActive);
        } else {
            log.error("更新自习室状态失败，ID：{}，状态：{}", id, isActive);
        }
        
        return result;
    }

    @Override
    public boolean checkPermission(Long studyRoomId, Long userId) {
        // 获取自习室信息
        StudyRoom studyRoom = this.getById(studyRoomId);
        if (studyRoom == null) {
            throw new BusinessException("自习室不存在");
        }
        
        // 获取用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 如果是管理员，直接返回true
        if (user.getUserType() == 1) {
            return true;
        }
        
        // 如果自习室归属为"全校"，所有学生都有权限
        if ("全校".equals(studyRoom.getBelongsTo())) {
            return true;
        }
        
        // 检查学生是否属于自习室所在院系
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUserId, userId);
        Admin admin = adminMapper.selectOne(queryWrapper);
        
        if (admin == null) {
            // 如果不是管理员，查询学生信息
            // 此处简化处理，实际中需要根据学生所属院系与自习室归属进行比较
            // 假设学生模型中有 college 字段表示所属学院
            return false;
        }
        
        return true;
    }
} 