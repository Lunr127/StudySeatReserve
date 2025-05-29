package com.studyseat.reserve.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.dto.SeatDTO;
import com.studyseat.reserve.dto.SeatQueryDTO;
import com.studyseat.reserve.entity.Seat;
import com.studyseat.reserve.mapper.SeatMapper;
import com.studyseat.reserve.service.impl.SeatServiceImpl;
import com.studyseat.reserve.vo.SeatVO;

@ExtendWith(MockitoExtension.class)
public class SeatServiceTest {

    @Mock
    private SeatMapper seatMapper;
    
    @InjectMocks
    private SeatServiceImpl seatService;
    
    private Seat seat;
    private SeatDTO seatDTO;
    private SeatVO seatVO;
    private SeatQueryDTO queryDTO;
    private List<SeatDTO> seatDTOList;
    private List<Seat> seatList;
    
    @BeforeEach
    void setUp() {
        // 设置座位实体
        seat = new Seat();
        seat.setId(1L);
        seat.setStudyRoomId(1L);
        seat.setSeatNumber("A-1");
        seat.setRowNumber(1);
        seat.setColumnNumber(1);
        seat.setHasPower(1);
        seat.setIsWindow(1);
        seat.setIsCorner(1);
        seat.setStatus(1);
        seat.setCreateTime(LocalDateTime.now());
        seat.setUpdateTime(LocalDateTime.now());
        
        // 设置座位DTO
        seatDTO = new SeatDTO();
        seatDTO.setId(1L);
        seatDTO.setStudyRoomId(1L);
        seatDTO.setSeatNumber("A-1");
        seatDTO.setRowNumber(1);
        seatDTO.setColumnNumber(1);
        seatDTO.setHasPower(1);
        seatDTO.setIsWindow(1);
        seatDTO.setIsCorner(1);
        seatDTO.setStatus(1);
        
        // 设置座位VO
        seatVO = new SeatVO();
        seatVO.setId(1L);
        seatVO.setStudyRoomId(1L);
        seatVO.setStudyRoomName("测试自习室");
        seatVO.setSeatNumber("A-1");
        seatVO.setRowNumber(1);
        seatVO.setColumnNumber(1);
        seatVO.setHasPower(1);
        seatVO.setHasPowerText("有");
        seatVO.setIsWindow(1);
        seatVO.setIsWindowText("是");
        seatVO.setIsCorner(1);
        seatVO.setIsCornerText("是");
        seatVO.setStatus(1);
        seatVO.setStatusText("正常");
        seatVO.setCreateTime(LocalDateTime.now());
        seatVO.setIsAvailable(true);
        
        // 设置查询条件
        queryDTO = new SeatQueryDTO();
        queryDTO.setStudyRoomId(1L);
        queryDTO.setHasPower(1);
        queryDTO.setIsWindow(1);
        queryDTO.setIsCorner(1);
        queryDTO.setStatus(1);
        queryDTO.setIsAvailable(true);
        
        // 设置批量座位DTO列表
        seatDTOList = new ArrayList<>();
        seatDTOList.add(seatDTO);
        
        // 设置座位列表
        seatList = new ArrayList<>();
        seatList.add(seat);
    }
    
    @Test
    void testAddSeat() {
        // 模拟插入
        when(seatMapper.insert(any(Seat.class))).thenReturn(1);
        
        // 执行添加座位
        boolean result = seatService.addSeat(seatDTO);
        
        // 验证结果
        assertTrue(result);
        
        // 验证调用
        verify(seatMapper).insert(any(Seat.class));
    }
    
    @Test
    void testBatchAddSeats() {
        // 模拟批量插入
        when(seatMapper.batchInsertSeats(anyList())).thenReturn(1);
        
        // 执行批量添加座位
        boolean result = seatService.batchAddSeats(seatDTOList);
        
        // 验证结果
        assertTrue(result);
        
        // 验证调用
        verify(seatMapper).batchInsertSeats(anyList());
    }
    
    @Test
    void testUpdateSeat() {
        // 模拟查询
        when(seatMapper.selectById(anyLong())).thenReturn(seat);
        
        // 模拟更新
        when(seatMapper.updateById(any(Seat.class))).thenReturn(1);
        
        // 执行更新座位
        boolean result = seatService.updateSeat(seatDTO);
        
        // 验证结果
        assertTrue(result);
        
        // 验证调用
        verify(seatMapper).selectById(anyLong());
        verify(seatMapper).updateById(any(Seat.class));
    }
    
    @Test
    void testUpdateSeatWithNonExistentId() {
        // 设置不存在的ID
        seatDTO.setId(999L);
        
        // 模拟查询（未找到）
        when(seatMapper.selectById(anyLong())).thenReturn(null);
        
        // 执行更新座位
        boolean result = seatService.updateSeat(seatDTO);
        
        // 验证结果
        assertFalse(result);
        
        // 验证调用
        verify(seatMapper).selectById(anyLong());
        verify(seatMapper, never()).updateById(any(Seat.class));
    }
    
    @Test
    void testUpdateSeatStatus() {
        // 模拟更新状态
        when(seatMapper.updateSeatStatus(anyLong(), anyInt())).thenReturn(1);
        
        // 执行更新座位状态
        boolean result = seatService.updateSeatStatus(1L, 0);
        
        // 验证结果
        assertTrue(result);
        
        // 验证调用
        verify(seatMapper).updateSeatStatus(anyLong(), anyInt());
    }
    
    @Test
    void testUpdateSeatStatusWithInvalidParams() {
        // 执行更新座位状态（无效的状态值）
        boolean result = seatService.updateSeatStatus(1L, 2);
        
        // 验证结果
        assertFalse(result);
        
        // 验证调用
        verify(seatMapper, never()).updateSeatStatus(anyLong(), anyInt());
    }
    
    @Test
    void testDeleteSeat() {
        // 模拟删除
        when(seatMapper.deleteById(anyLong())).thenReturn(1);
        
        // 执行删除座位
        boolean result = seatService.deleteSeat(1L);
        
        // 验证结果
        assertTrue(result);
        
        // 验证调用
        verify(seatMapper).deleteById(anyLong());
    }
    
    @Test
    void testDeleteAllSeatsInRoom() {
        // 模拟删除
        when(seatMapper.delete(any())).thenReturn(10);
        
        // 执行删除自习室所有座位
        boolean result = seatService.deleteAllSeatsInRoom(1L);
        
        // 验证结果
        assertTrue(result);
        
        // 验证调用
        verify(seatMapper).delete(any());
    }
    
    @Test
    void testGetSeatsByRoomId() {
        // 创建分页对象
        Page<Seat> page = new Page<>(1, 10);
        page.setRecords(seatList);
        page.setTotal(1);
        
        // 模拟分页查询
        when(seatMapper.selectPage(any(), any())).thenReturn(page);
        
        // 执行查询
        IPage<Seat> result = seatService.getSeatsByRoomId(new Page<>(1, 10), 1L);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(seat.getId(), result.getRecords().get(0).getId());
        
        // 验证调用
        verify(seatMapper).selectPage(any(), any());
    }
    
    @Test
    void testQuerySeatsByCondition() {
        // 创建分页对象
        Page<SeatVO> page = new Page<>(1, 10);
        page.setRecords(List.of(seatVO));
        page.setTotal(1);
        
        // 模拟条件查询
        when(seatMapper.querySeatsByCondition(any(), any())).thenReturn(page);
        
        // 执行查询
        IPage<SeatVO> result = seatService.querySeatsByCondition(new Page<>(1, 10), queryDTO);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(seatVO.getId(), result.getRecords().get(0).getId());
        
        // 验证调用
        verify(seatMapper).querySeatsByCondition(any(), any());
    }
    
    @Test
    void testGetSeatVOById() {
        // 模拟查询
        when(seatMapper.getSeatVOById(anyLong())).thenReturn(seatVO);
        
        // 执行查询
        SeatVO result = seatService.getSeatVOById(1L);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(seatVO.getId(), result.getId());
        assertEquals(seatVO.getSeatNumber(), result.getSeatNumber());
        
        // 验证调用
        verify(seatMapper).getSeatVOById(anyLong());
    }
    
    @Test
    void testGenerateSeats() {
        // 模拟清空座位
        when(seatMapper.delete(any())).thenReturn(0);
        
        // 模拟批量插入座位
        when(seatMapper.batchInsertSeats(anyList())).thenReturn(4);
        
        // 执行生成座位
        boolean result = seatService.generateSeats(1L, 2, 2, 1);
        
        // 验证结果
        assertTrue(result);
        
        // 验证调用
        verify(seatMapper).delete(any());
        verify(seatMapper).batchInsertSeats(anyList());
    }
    
    @Test
    void testGenerateSeatsWithInvalidParams() {
        // 执行生成座位（无效参数）
        boolean result = seatService.generateSeats(1L, -1, 2, 1);
        
        // 验证结果
        assertFalse(result);
        
        // 验证调用
        verify(seatMapper, never()).delete(any());
        verify(seatMapper, never()).batchInsertSeats(anyList());
    }
} 