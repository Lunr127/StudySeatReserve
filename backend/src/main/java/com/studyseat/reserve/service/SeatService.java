package com.studyseat.reserve.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.studyseat.reserve.entity.Seat;

/**
 * 座位服务接口
 */
public interface SeatService extends IService<Seat> {
    
    /**
     * 根据自习室ID获取座位列表
     * 
     * @param page 分页参数
     * @param roomId 自习室ID
     * @return 座位分页列表
     */
    IPage<Seat> getSeatsByRoomId(Page<Seat> page, Long roomId);
    
} 