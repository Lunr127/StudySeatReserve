package com.studyseat.reserve.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.studyseat.reserve.dto.SeatDTO;
import com.studyseat.reserve.dto.SeatQueryDTO;
import com.studyseat.reserve.entity.Seat;
import com.studyseat.reserve.mapper.SeatMapper;
import com.studyseat.reserve.service.SeatService;
import com.studyseat.reserve.vo.SeatVO;

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
    
    @Override
    public IPage<SeatVO> querySeatsByCondition(Page<SeatVO> page, SeatQueryDTO query) {
        return baseMapper.querySeatsByCondition(page, query);
    }
    
    @Override
    public SeatVO getSeatVOById(Long id) {
        return baseMapper.getSeatVOById(id);
    }
    
    @Override
    @Transactional
    public boolean addSeat(SeatDTO seatDTO) {
        Seat seat = new Seat();
        BeanUtils.copyProperties(seatDTO, seat);
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        seat.setCreateTime(now);
        seat.setUpdateTime(now);
        
        return this.save(seat);
    }
    
    @Override
    @Transactional
    public boolean batchAddSeats(List<SeatDTO> seatDTOs) {
        if (seatDTOs == null || seatDTOs.isEmpty()) {
            return false;
        }
        
        List<Seat> seats = seatDTOs.stream().map(dto -> {
            Seat seat = new Seat();
            BeanUtils.copyProperties(dto, seat);
            
            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            seat.setCreateTime(now);
            seat.setUpdateTime(now);
            
            return seat;
        }).collect(Collectors.toList());
        
        return baseMapper.batchInsertSeats(seats) > 0;
    }
    
    @Override
    @Transactional
    public boolean generateSeats(Long studyRoomId, Integer rows, Integer columns, Integer hasPower) {
        if (studyRoomId == null || rows == null || columns == null || hasPower == null
                || rows <= 0 || columns <= 0 || (hasPower != 0 && hasPower != 1)) {
            return false;
        }
        
        // 先清除该自习室下的所有座位
        deleteAllSeatsInRoom(studyRoomId);
        
        // 生成座位
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= columns; j++) {
                Seat seat = new Seat();
                seat.setStudyRoomId(studyRoomId);
                seat.setSeatNumber(i + "-" + j); // 座位编号格式：行-列
                seat.setRowNumber(i);
                seat.setColumnNumber(j);
                seat.setHasPower(hasPower);
                
                // 判断是否靠窗和角落
                seat.setIsWindow(isWindowSeat(i, j, rows, columns) ? 1 : 0);
                seat.setIsCorner(isCornerSeat(i, j, rows, columns) ? 1 : 0);
                
                seat.setStatus(1); // 默认状态为正常
                
                // 设置创建时间和更新时间
                LocalDateTime now = LocalDateTime.now();
                seat.setCreateTime(now);
                seat.setUpdateTime(now);
                
                seats.add(seat);
            }
        }
        
        return baseMapper.batchInsertSeats(seats) > 0;
    }
    
    @Override
    @Transactional
    public boolean updateSeat(SeatDTO seatDTO) {
        if (seatDTO == null || seatDTO.getId() == null) {
            return false;
        }
        
        Seat seat = this.getById(seatDTO.getId());
        if (seat == null) {
            return false;
        }
        
        BeanUtils.copyProperties(seatDTO, seat);
        seat.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(seat);
    }
    
    @Override
    @Transactional
    public boolean updateSeatStatus(Long id, Integer status) {
        if (id == null || status == null || (status != 0 && status != 1)) {
            return false;
        }
        
        return baseMapper.updateSeatStatus(id, status) > 0;
    }
    
    @Override
    @Transactional
    public boolean deleteSeat(Long id) {
        if (id == null) {
            return false;
        }
        
        return this.removeById(id);
    }
    
    @Override
    @Transactional
    public boolean deleteAllSeatsInRoom(Long studyRoomId) {
        if (studyRoomId == null) {
            return false;
        }
        
        LambdaQueryWrapper<Seat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Seat::getStudyRoomId, studyRoomId);
        
        return this.remove(queryWrapper);
    }
    
    /**
     * 判断是否是靠窗的座位
     * 
     * @param row 行号
     * @param column 列号
     * @param rows 总行数
     * @param columns 总列数
     * @return 是否靠窗
     */
    private boolean isWindowSeat(int row, int column, int rows, int columns) {
        return row == 1 || row == rows || column == 1 || column == columns;
    }
    
    /**
     * 判断是否是角落的座位
     * 
     * @param row 行号
     * @param column 列号
     * @param rows 总行数
     * @param columns 总列数
     * @return 是否角落
     */
    private boolean isCornerSeat(int row, int column, int rows, int columns) {
        return (row == 1 && column == 1) || (row == 1 && column == columns) 
                || (row == rows && column == 1) || (row == rows && column == columns);
    }
} 