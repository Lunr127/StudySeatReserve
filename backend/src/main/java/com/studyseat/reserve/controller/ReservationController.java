package com.studyseat.reserve.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.common.Result;
import com.studyseat.reserve.dto.ReservationDTO;
import com.studyseat.reserve.dto.ReservationQueryDTO;
import com.studyseat.reserve.entity.Student;
import com.studyseat.reserve.exception.BusinessException;
import com.studyseat.reserve.mapper.StudentMapper;
import com.studyseat.reserve.service.ReservationService;
import com.studyseat.reserve.util.JwtUtil;
import com.studyseat.reserve.vo.ReservationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 预约控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    
    private final ReservationService reservationService;
    private final StudentMapper studentMapper;
    private final JwtUtil jwtUtil;
    
    /**
     * 创建预约
     * 
     * @param reservationDTO 预约数据
     * @param request HTTP请求
     * @return 预约ID
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Long> createReservation(@RequestBody @Validated ReservationDTO reservationDTO, HttpServletRequest request) {
        // 从JWT中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(request);
        
        // 获取学生ID
        Student student = studentMapper.selectByUserId(userId);
        if (student == null) {
            throw new BusinessException("学生信息不存在");
        }
        
        Long reservationId = reservationService.createReservation(student.getId(), reservationDTO);
        return Result.ok(reservationId, "预约成功");
    }
    
    /**
     * 取消预约
     * 
     * @param id 预约ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Boolean> cancelReservation(@PathVariable Long id, HttpServletRequest request) {
        // 从JWT中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(request);
        
        // 获取学生ID
        Student student = studentMapper.selectByUserId(userId);
        if (student == null) {
            throw new BusinessException("学生信息不存在");
        }
        
        boolean result = reservationService.cancelReservation(id, student.getId());
        return Result.ok(result, "取消预约成功");
    }
    
    /**
     * 延长预约时间
     * 
     * @param id 预约ID
     * @param endTimeStr 新的结束时间字符串
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/{id}/extend")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Boolean> extendReservation(
            @PathVariable Long id,
            @RequestParam String endTimeStr,
            HttpServletRequest request) {
        // 从JWT中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(request);
        
        // 获取学生ID
        Student student = studentMapper.selectByUserId(userId);
        if (student == null) {
            throw new BusinessException("学生信息不存在");
        }
        
        // 解析结束时间
        LocalDateTime endTime;
        try {
            endTime = LocalDateTime.parse(endTimeStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            throw new BusinessException("结束时间格式不正确");
        }
        
        boolean result = reservationService.extendReservation(id, student.getId(), endTime);
        return Result.ok(result, "延长预约成功");
    }
    
    /**
     * 分页查询预约列表
     * 
     * @param queryDTO 查询条件
     * @param current 当前页
     * @param size 页大小
     * @param request HTTP请求
     * @return 预约分页列表
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public Result<IPage<ReservationVO>> getReservationPage(
            ReservationQueryDTO queryDTO,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        // 从JWT中获取用户ID和用户类型
        Long userId = jwtUtil.getUserIdFromToken(request);
        String userType = jwtUtil.getUserTypeFromToken(request);
        
        // 学生只能查看自己的预约
        if ("STUDENT".equals(userType)) {
            Student student = studentMapper.selectByUserId(userId);
            if (student == null) {
                throw new BusinessException("学生信息不存在");
            }
            queryDTO.setStudentId(student.getId());
        }
        
        Page<ReservationVO> page = new Page<>(current, size);
        IPage<ReservationVO> reservationPage = reservationService.getReservationPage(queryDTO, page);
        
        return Result.ok(reservationPage);
    }
    
    /**
     * 根据ID查询预约详情
     * 
     * @param id 预约ID
     * @param request HTTP请求
     * @return 预约详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public Result<ReservationVO> getReservationById(@PathVariable Long id, HttpServletRequest request) {
        // 从JWT中获取用户ID和用户类型
        Long userId = jwtUtil.getUserIdFromToken(request);
        String userType = jwtUtil.getUserTypeFromToken(request);
        
        ReservationVO reservation = reservationService.getReservationById(id);
        if (reservation == null) {
            throw new BusinessException("预约不存在");
        }
        
        // 学生只能查看自己的预约
        if ("STUDENT".equals(userType)) {
            Student student = studentMapper.selectByUserId(userId);
            if (student == null || !student.getId().equals(reservation.getStudentId())) {
                throw new BusinessException("无权查看该预约");
            }
        }
        
        return Result.ok(reservation);
    }
    
    /**
     * 获取当前学生的有效预约
     * 
     * @param request HTTP请求
     * @return 预约列表
     */
    @GetMapping("/current")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<List<ReservationVO>> getCurrentReservations(HttpServletRequest request) {
        // 从JWT中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(request);
        
        // 获取学生ID
        Student student = studentMapper.selectByUserId(userId);
        if (student == null) {
            throw new BusinessException("学生信息不存在");
        }
        
        List<ReservationVO> reservations = reservationService.getCurrentReservations(student.getId());
        
        return Result.ok(reservations);
    }
    
    /**
     * 检查座位在指定时间段是否可预约
     * 
     * @param seatId 座位ID
     * @param startTimeStr 开始时间字符串
     * @param endTimeStr 结束时间字符串
     * @return 是否可预约
     */
    @GetMapping("/check-availability")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Boolean> checkSeatAvailability(
            @RequestParam Long seatId,
            @RequestParam String startTimeStr,
            @RequestParam String endTimeStr) {
        // 解析时间
        LocalDateTime startTime, endTime;
        try {
            startTime = LocalDateTime.parse(startTimeStr, DateTimeFormatter.ISO_DATE_TIME);
            endTime = LocalDateTime.parse(endTimeStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            throw new BusinessException("时间格式不正确");
        }
        
        boolean available = reservationService.checkSeatAvailable(seatId, startTime, endTime);
        
        return Result.ok(available);
    }
    
    /**
     * 查询某个座位今天的预约情况
     * 
     * @param seatId 座位ID
     * @return 预约列表
     */
    @GetMapping("/seat/{seatId}/today")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public Result<List<ReservationVO>> getTodayReservationsBySeat(@PathVariable Long seatId) {
        List<ReservationVO> reservations = reservationService.getTodayReservationsBySeat(seatId);
        return Result.ok(reservations);
    }
} 