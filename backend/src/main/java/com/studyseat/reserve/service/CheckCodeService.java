package com.studyseat.reserve.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.vo.CheckCodeVO;

import java.time.LocalDate;

/**
 * 签到码服务接口
 */
public interface CheckCodeService {
    
    /**
     * 生成每日签到码
     * 
     * @param studyRoomId 自习室ID
     * @param validDate 有效日期
     * @return 签到码
     */
    String generateDailyCode(Long studyRoomId, LocalDate validDate);
    
    /**
     * 批量生成签到码（为所有自习室生成指定日期的签到码）
     * 
     * @param validDate 有效日期
     * @return 生成数量
     */
    Integer generateDailyCodesForAllRooms(LocalDate validDate);
    
    /**
     * 验证签到码
     * 
     * @param code 签到码
     * @param studyRoomId 自习室ID
     * @param validDate 验证日期
     * @return 验证结果
     */
    Boolean validateCheckCode(String code, Long studyRoomId, LocalDate validDate);
    
    /**
     * 获取自习室当日签到码
     * 
     * @param studyRoomId 自习室ID
     * @param date 日期
     * @return 签到码信息
     */
    CheckCodeVO getTodayCheckCode(Long studyRoomId, LocalDate date);
    
    /**
     * 根据ID获取签到码详情
     * 
     * @param id 签到码ID
     * @return 签到码详情
     */
    CheckCodeVO getCheckCodeById(Long id);
    
    /**
     * 分页查询签到码
     * 
     * @param page 分页参数
     * @param studyRoomId 自习室ID（可选）
     * @param validDate 有效日期（可选）
     * @return 签到码分页
     */
    IPage<CheckCodeVO> getCheckCodePage(Page<CheckCodeVO> page, Long studyRoomId, LocalDate validDate);
    
    /**
     * 禁用签到码
     * 
     * @param id 签到码ID
     * @return 操作结果
     */
    Boolean disableCheckCode(Long id);
    
    /**
     * 启用签到码
     * 
     * @param id 签到码ID
     * @return 操作结果
     */
    Boolean enableCheckCode(Long id);
} 