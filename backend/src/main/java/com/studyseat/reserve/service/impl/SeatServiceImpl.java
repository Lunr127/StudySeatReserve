package com.studyseat.reserve.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyseat.reserve.entity.Seat;
import com.studyseat.reserve.mapper.SeatMapper;
import com.studyseat.reserve.service.SeatService;

/**
 * 座位服务实现类
 */
@Service
public class SeatServiceImpl extends ServiceImpl<SeatMapper, Seat> implements SeatService {

    @Override
    public IPage<Seat> getSeatsByRoomId(Page<Seat> page, Long roomId) {
        LambdaQueryWrapper<Seat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Seat::getStudyRoomId, roomId)
                    .eq(Seat::getStatus, 1) // 只查询状态正常的座位
                    .orderByAsc(Seat::getRowNumber)
                    .orderByAsc(Seat::getColumnNumber);
        
        return this.page(page, queryWrapper);
    }
} 