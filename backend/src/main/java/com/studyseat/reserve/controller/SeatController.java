package com.studyseat.reserve.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.common.Result;
import com.studyseat.reserve.dto.SeatDTO;
import com.studyseat.reserve.dto.SeatQueryDTO;
import com.studyseat.reserve.entity.Seat;
import com.studyseat.reserve.service.SeatService;
import com.studyseat.reserve.vo.SeatVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 座位控制器
 */
@Api(tags = "座位管理")
@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;
    
    /**
     * 根据自习室ID获取座位列表
     */
    @ApiOperation(value = "根据自习室ID获取座位列表")
    @GetMapping("/study-room/{roomId}")
    public Result<IPage<Seat>> getSeatsByRoomId(
            @ApiParam(value = "自习室ID", required = true) @PathVariable Long roomId,
            @ApiParam(value = "当前页", required = true) @RequestParam(defaultValue = "1") long current,
            @ApiParam(value = "每页大小", required = true) @RequestParam(defaultValue = "10") long size) {
        
        Page<Seat> page = new Page<>(current, size);
        IPage<Seat> seats = seatService.getSeatsByRoomId(page, roomId);
        
        return Result.success(seats);
    }
    
    /**
     * 获取座位详情
     */
    @ApiOperation(value = "获取座位详情")
    @GetMapping("/{id}")
    public Result<SeatVO> getSeatById(
            @ApiParam(value = "座位ID", required = true) @PathVariable Long id) {
        
        SeatVO seat = seatService.getSeatVOById(id);
        if (seat == null) {
            return Result.failure("座位不存在");
        }
        
        return Result.success(seat);
    }
    
    /**
     * 高级查询座位
     */
    @ApiOperation(value = "高级查询座位")
    @PostMapping("/query")
    public Result<IPage<SeatVO>> querySeatsByCondition(
            @ApiParam(value = "查询条件", required = true) @RequestBody SeatQueryDTO query,
            @ApiParam(value = "当前页", required = true) @RequestParam(defaultValue = "1") long current,
            @ApiParam(value = "每页大小", required = true) @RequestParam(defaultValue = "10") long size) {
        
        Page<SeatVO> page = new Page<>(current, size);
        IPage<SeatVO> seats = seatService.querySeatsByCondition(page, query);
        
        return Result.success(seats);
    }
    
    /**
     * 添加座位
     */
    @ApiOperation(value = "添加座位")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result<Void> addSeat(
            @ApiParam(value = "座位数据", required = true) @RequestBody SeatDTO seatDTO) {
        
        boolean success = seatService.addSeat(seatDTO);
        if (success) {
            return Result.success();
        } else {
            return Result.failure("添加座位失败");
        }
    }
    
    /**
     * 批量添加座位
     */
    @ApiOperation(value = "批量添加座位")
    @PostMapping("/batch")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result<Void> batchAddSeats(
            @ApiParam(value = "座位数据列表", required = true) @RequestBody List<SeatDTO> seatDTOs) {
        
        boolean success = seatService.batchAddSeats(seatDTOs);
        if (success) {
            return Result.success();
        } else {
            return Result.failure("批量添加座位失败");
        }
    }
    
    /**
     * 生成座位
     */
    @ApiOperation(value = "生成座位（根据行列数自动生成）")
    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result<Void> generateSeats(
            @ApiParam(value = "自习室ID", required = true) @RequestParam Long studyRoomId,
            @ApiParam(value = "行数", required = true) @RequestParam Integer rows,
            @ApiParam(value = "列数", required = true) @RequestParam Integer columns,
            @ApiParam(value = "是否有电源（0-无，1-有）", required = true) @RequestParam Integer hasPower) {
        
        boolean success = seatService.generateSeats(studyRoomId, rows, columns, hasPower);
        if (success) {
            return Result.success();
        } else {
            return Result.failure("生成座位失败");
        }
    }
    
    /**
     * 更新座位
     */
    @ApiOperation(value = "更新座位")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result<Void> updateSeat(
            @ApiParam(value = "座位ID", required = true) @PathVariable Long id,
            @ApiParam(value = "座位数据", required = true) @RequestBody SeatDTO seatDTO) {
        
        seatDTO.setId(id);
        boolean success = seatService.updateSeat(seatDTO);
        if (success) {
            return Result.success();
        } else {
            return Result.failure("更新座位失败");
        }
    }
    
    /**
     * 更新座位状态
     */
    @ApiOperation(value = "更新座位状态")
    @PutMapping("/{id}/status/{status}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result<Void> updateSeatStatus(
            @ApiParam(value = "座位ID", required = true) @PathVariable Long id,
            @ApiParam(value = "状态（0-停用，1-正常）", required = true) @PathVariable Integer status) {
        
        boolean success = seatService.updateSeatStatus(id, status);
        if (success) {
            return Result.success();
        } else {
            return Result.failure("更新座位状态失败");
        }
    }
    
    /**
     * 删除座位
     */
    @ApiOperation(value = "删除座位")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result<Void> deleteSeat(
            @ApiParam(value = "座位ID", required = true) @PathVariable Long id) {
        
        boolean success = seatService.deleteSeat(id);
        if (success) {
            return Result.success();
        } else {
            return Result.failure("删除座位失败");
        }
    }
    
    /**
     * 删除自习室下所有座位
     */
    @ApiOperation(value = "删除自习室下所有座位")
    @DeleteMapping("/study-room/{roomId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result<Void> deleteAllSeatsInRoom(
            @ApiParam(value = "自习室ID", required = true) @PathVariable Long roomId) {
        
        boolean success = seatService.deleteAllSeatsInRoom(roomId);
        if (success) {
            return Result.success();
        } else {
            return Result.failure("删除自习室下所有座位失败");
        }
    }
} 