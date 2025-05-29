package com.studyseat.reserve.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.studyseat.reserve.common.Result;
import com.studyseat.reserve.service.CheckCodeService;
import com.studyseat.reserve.vo.CheckCodeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 签到码控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/check-code")
@RequiredArgsConstructor
public class CheckCodeController {
    
    private final CheckCodeService checkCodeService;
    
    /**
     * 生成每日签到码
     * 
     * @param studyRoomId 自习室ID
     * @param validDate 有效日期
     * @return 签到码
     */
    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> generateDailyCode(
            @RequestParam Long studyRoomId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate validDate) {
        
        String code = checkCodeService.generateDailyCode(studyRoomId, validDate);
        return Result.ok(code, "签到码生成成功");
    }
    
    /**
     * 批量生成签到码（为所有自习室生成指定日期的签到码）
     * 
     * @param validDate 有效日期
     * @return 生成数量
     */
    @PostMapping("/generate-all")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Integer> generateDailyCodesForAllRooms(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate validDate) {
        
        Integer count = checkCodeService.generateDailyCodesForAllRooms(validDate);
        return Result.ok(count, "批量生成签到码成功，共生成 " + count + " 个");
    }
    
    /**
     * 获取自习室当日签到码
     * 
     * @param studyRoomId 自习室ID
     * @param date 日期（可选，默认今天）
     * @return 签到码信息
     */
    @GetMapping("/today/{studyRoomId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public Result<CheckCodeVO> getTodayCheckCode(
            @PathVariable Long studyRoomId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        
        if (date == null) {
            date = LocalDate.now();
        }
        
        CheckCodeVO checkCodeVO = checkCodeService.getTodayCheckCode(studyRoomId, date);
        return Result.ok(checkCodeVO);
    }
    
    /**
     * 分页查询签到码
     * 
     * @param studyRoomId 自习室ID（可选）
     * @param validDate 有效日期（可选）
     * @param current 当前页
     * @param size 页大小
     * @return 签到码分页
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<CheckCodeVO>> getCheckCodePage(
            @RequestParam(required = false) Long studyRoomId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate validDate,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Page<CheckCodeVO> page = new Page<>(current, size);
        IPage<CheckCodeVO> checkCodePage = checkCodeService.getCheckCodePage(page, studyRoomId, validDate);
        
        return Result.ok(checkCodePage);
    }
    
    /**
     * 禁用签到码
     * 
     * @param id 签到码ID
     * @return 操作结果
     */
    @PostMapping("/disable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> disableCheckCode(@PathVariable Long id) {
        boolean result = checkCodeService.disableCheckCode(id);
        return Result.ok(result, "签到码已禁用");
    }
    
    /**
     * 启用签到码
     * 
     * @param id 签到码ID
     * @return 操作结果
     */
    @PostMapping("/enable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> enableCheckCode(@PathVariable Long id) {
        boolean result = checkCodeService.enableCheckCode(id);
        return Result.ok(result, "签到码已启用");
    }
    
    /**
     * 获取签到码的二维码数据
     * 
     * @param id 签到码ID
     * @return 二维码数据
     */
    @GetMapping("/qr-data/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> getQRCodeData(@PathVariable Long id) {
        CheckCodeVO checkCodeVO = checkCodeService.getCheckCodeById(id);
        if (checkCodeVO == null) {
            return Result.error("签到码不存在");
        }
        
        // 构造二维码数据
        Map<String, Object> qrData = new HashMap<>();
        qrData.put("code", checkCodeVO.getCode());
        qrData.put("studyRoomId", checkCodeVO.getStudyRoomId());
        qrData.put("studyRoomName", checkCodeVO.getStudyRoomName());
        qrData.put("validDate", checkCodeVO.getValidDate());
        qrData.put("type", "check_in_code");
        
        // 转换为JSON字符串
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String qrDataJson = objectMapper.writeValueAsString(qrData);
            return Result.ok(qrDataJson, "获取二维码数据成功");
        } catch (Exception e) {
            log.error("生成二维码数据失败", e);
            return Result.error("生成二维码数据失败");
        }
    }
    
    /**
     * 验证签到码
     * 
     * @param code 签到码
     * @param studyRoomId 自习室ID
     * @param validDate 验证日期（可选，默认今天）
     * @return 验证结果
     */
    @GetMapping("/validate")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Boolean> validateCheckCode(
            @RequestParam String code,
            @RequestParam Long studyRoomId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate validDate) {
        
        if (validDate == null) {
            validDate = LocalDate.now();
        }
        
        boolean isValid = checkCodeService.validateCheckCode(code, studyRoomId, validDate);
        return Result.ok(isValid, isValid ? "签到码有效" : "签到码无效");
    }
} 