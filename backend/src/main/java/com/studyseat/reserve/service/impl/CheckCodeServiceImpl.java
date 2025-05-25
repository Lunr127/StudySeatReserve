package com.studyseat.reserve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyseat.reserve.entity.CheckCode;
import com.studyseat.reserve.entity.StudyRoom;
import com.studyseat.reserve.exception.BusinessException;
import com.studyseat.reserve.mapper.CheckCodeMapper;
import com.studyseat.reserve.mapper.StudyRoomMapper;
import com.studyseat.reserve.service.CheckCodeService;
import com.studyseat.reserve.vo.CheckCodeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * 签到码服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckCodeServiceImpl extends ServiceImpl<CheckCodeMapper, CheckCode> implements CheckCodeService {
    
    private final CheckCodeMapper checkCodeMapper;
    private final StudyRoomMapper studyRoomMapper;
    
    @Override
    @Transactional
    public String generateDailyCode(Long studyRoomId, LocalDate validDate) {
        // 1. 验证自习室是否存在
        StudyRoom studyRoom = studyRoomMapper.selectById(studyRoomId);
        if (studyRoom == null) {
            throw new BusinessException("自习室不存在");
        }
        
        // 2. 检查是否已经生成过该日期的签到码
        CheckCode existingCode = checkCodeMapper.selectByRoomIdAndDate(studyRoomId, validDate);
        if (existingCode != null) {
            log.info("自习室 {} 在 {} 的签到码已存在: {}", studyRoomId, validDate, existingCode.getCode());
            return existingCode.getCode();
        }
        
        // 3. 生成6位数字签到码
        String code = generateRandomCode();
        
        // 4. 确保签到码唯一性（同一天内不重复）
        while (isCodeExistsForDate(code, validDate)) {
            code = generateRandomCode();
        }
        
        // 5. 保存签到码
        CheckCode checkCode = new CheckCode();
        checkCode.setStudyRoomId(studyRoomId);
        checkCode.setCode(code);
        checkCode.setValidDate(validDate);
        checkCode.setIsActive(1);
        checkCode.setCreateTime(LocalDateTime.now());
        checkCode.setUpdateTime(LocalDateTime.now());
        
        checkCodeMapper.insert(checkCode);
        
        log.info("为自习室 {} 生成 {} 的签到码: {}", studyRoomId, validDate, code);
        
        return code;
    }
    
    @Override
    @Transactional
    public Integer generateDailyCodesForAllRooms(LocalDate validDate) {
        // 1. 获取所有开放的自习室
        LambdaQueryWrapper<StudyRoom> query = new LambdaQueryWrapper<>();
        query.eq(StudyRoom::getIsActive, 1);
        List<StudyRoom> studyRooms = studyRoomMapper.selectList(query);
        
        int generatedCount = 0;
        
        // 2. 为每个自习室生成签到码
        for (StudyRoom studyRoom : studyRooms) {
            try {
                generateDailyCode(studyRoom.getId(), validDate);
                generatedCount++;
            } catch (Exception e) {
                log.error("为自习室 {} 生成签到码失败: {}", studyRoom.getId(), e.getMessage());
            }
        }
        
        log.info("批量生成签到码完成，日期: {}, 成功生成: {} 个", validDate, generatedCount);
        
        return generatedCount;
    }
    
    @Override
    public Boolean validateCheckCode(String code, Long studyRoomId, LocalDate validDate) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        
        CheckCodeVO checkCodeVO = checkCodeMapper.selectByCodeAndDate(code.trim(), validDate);
        
        if (checkCodeVO == null) {
            return false;
        }
        
        // 验证自习室ID是否匹配
        return checkCodeVO.getStudyRoomId().equals(studyRoomId);
    }
    
    @Override
    public CheckCodeVO getTodayCheckCode(Long studyRoomId, LocalDate date) {
        CheckCode checkCode = checkCodeMapper.selectByRoomIdAndDate(studyRoomId, date);
        if (checkCode == null) {
            return null;
        }
        
        CheckCodeVO vo = new CheckCodeVO();
        vo.setId(checkCode.getId());
        vo.setStudyRoomId(checkCode.getStudyRoomId());
        vo.setCode(checkCode.getCode());
        vo.setValidDate(checkCode.getValidDate());
        vo.setIsActive(checkCode.getIsActive());
        vo.setStatusText(checkCode.getIsActive() == 1 ? "有效" : "无效");
        vo.setCreateTime(checkCode.getCreateTime());
        
        return vo;
    }
    
    @Override
    public IPage<CheckCodeVO> getCheckCodePage(Page<CheckCodeVO> page, Long studyRoomId, LocalDate validDate) {
        return checkCodeMapper.selectCheckCodePage(page, studyRoomId, validDate);
    }
    
    @Override
    @Transactional
    public Boolean disableCheckCode(Long id) {
        CheckCode checkCode = checkCodeMapper.selectById(id);
        if (checkCode == null) {
            throw new BusinessException("签到码不存在");
        }
        
        checkCode.setIsActive(0);
        checkCode.setUpdateTime(LocalDateTime.now());
        
        int result = checkCodeMapper.updateById(checkCode);
        
        log.info("禁用签到码: {}", checkCode.getCode());
        
        return result > 0;
    }
    
    @Override
    @Transactional
    public Boolean enableCheckCode(Long id) {
        CheckCode checkCode = checkCodeMapper.selectById(id);
        if (checkCode == null) {
            throw new BusinessException("签到码不存在");
        }
        
        checkCode.setIsActive(1);
        checkCode.setUpdateTime(LocalDateTime.now());
        
        int result = checkCodeMapper.updateById(checkCode);
        
        log.info("启用签到码: {}", checkCode.getCode());
        
        return result > 0;
    }
    
    /**
     * 生成6位随机数字签到码
     */
    private String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 生成100000-999999之间的数字
        return String.valueOf(code);
    }
    
    /**
     * 检查签到码在指定日期是否已存在
     */
    private boolean isCodeExistsForDate(String code, LocalDate validDate) {
        LambdaQueryWrapper<CheckCode> query = new LambdaQueryWrapper<>();
        query.eq(CheckCode::getCode, code)
             .eq(CheckCode::getValidDate, validDate);
        
        return checkCodeMapper.selectCount(query) > 0;
    }
} 