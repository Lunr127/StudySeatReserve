package com.studyseat.reserve.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.entity.Violation;
import com.studyseat.reserve.exception.BusinessException;
import com.studyseat.reserve.mapper.ViolationMapper;
import com.studyseat.reserve.service.ViolationService;
import com.studyseat.reserve.vo.ViolationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 违约记录服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ViolationServiceImpl implements ViolationService {
    
    private final ViolationMapper violationMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createViolation(Long studentId, Long reservationId, Integer violationType, String description) {
        // 参数校验
        if (studentId == null || reservationId == null || violationType == null) {
            throw new BusinessException("参数不能为空");
        }
        
        if (violationType < 1 || violationType > 3) {
            throw new BusinessException("违约类型无效");
        }
        
        // 创建违约记录
        Violation violation = new Violation();
        violation.setStudentId(studentId);
        violation.setReservationId(reservationId);
        violation.setViolationType(violationType);
        violation.setDescription(description);
        violation.setCreateTime(LocalDateTime.now());
        violation.setUpdateTime(LocalDateTime.now());
        
        violationMapper.insert(violation);
        
        log.info("创建违约记录成功，学生ID: {}, 预约ID: {}, 违约类型: {}", studentId, reservationId, violationType);
        
        return violation.getId();
    }
    
    @Override
    public IPage<ViolationVO> getViolationPageByStudentId(Long studentId, Page<ViolationVO> page) {
        if (studentId == null) {
            throw new BusinessException("学生ID不能为空");
        }
        
        return violationMapper.selectViolationPageByStudentId(page, studentId);
    }
    
    @Override
    public Integer getViolationCountByStudentId(Long studentId) {
        if (studentId == null) {
            throw new BusinessException("学生ID不能为空");
        }
        
        Integer count = violationMapper.countViolationByStudentId(studentId);
        return count != null ? count : 0;
    }
    
    @Override
    public ViolationVO getViolationById(Long violationId) {
        if (violationId == null) {
            throw new BusinessException("违约记录ID不能为空");
        }
        
        // 这里需要通过Mapper查询详细信息，暂时返回null
        // 实际实现需要在ViolationMapper中添加相应的查询方法
        return null;
    }
} 