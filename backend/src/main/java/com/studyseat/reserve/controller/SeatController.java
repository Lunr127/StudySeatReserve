package com.studyseat.reserve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.common.Result;
import com.studyseat.reserve.entity.Seat;
import com.studyseat.reserve.service.SeatService;

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
    public Result<Seat> getSeatById(
            @ApiParam(value = "座位ID", required = true) @PathVariable Long id) {
        
        Seat seat = seatService.getById(id);
        if (seat == null) {
            return Result.failure("座位不存在");
        }
        
        return Result.success(seat);
    }
} 