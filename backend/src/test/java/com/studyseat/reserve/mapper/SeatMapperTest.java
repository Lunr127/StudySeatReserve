package com.studyseat.reserve.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.dto.SeatQueryDTO;
import com.studyseat.reserve.entity.Seat;
import com.studyseat.reserve.vo.SeatVO;

/**
 * 座位数据访问层测试类
 * 
 * 注意：这个测试类使用了实际的H2数据库，需要配置测试用的数据源
 */
@SpringBootTest
@ActiveProfiles("test") // 使用测试配置文件
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional // 测试完成后回滚事务
public class SeatMapperTest {

    @Autowired
    private SeatMapper seatMapper;
    
    @Autowired
    private StudyRoomMapper studyRoomMapper;
    
    /**
     * 测试基本的插入和查询操作
     */
    @Test
    void testInsertAndSelect() {
        // 创建座位
        Seat seat = createSeat(1L, "A-1", 1, 1);
        
        // 插入并验证
        int result = seatMapper.insert(seat);
        assertEquals(1, result);
        assertNotNull(seat.getId());
        
        // 查询并验证
        Seat selected = seatMapper.selectById(seat.getId());
        assertNotNull(selected);
        assertEquals(seat.getSeatNumber(), selected.getSeatNumber());
        assertEquals(seat.getRowNumber(), selected.getRowNumber());
        assertEquals(seat.getColumnNumber(), selected.getColumnNumber());
    }
    
    /**
     * 测试更新操作
     */
    @Test
    void testUpdate() {
        // 先插入座位
        Seat seat = createSeat(1L, "A-1", 1, 1);
        seatMapper.insert(seat);
        
        // 修改属性
        seat.setSeatNumber("A-2");
        seat.setHasPower(0);
        
        // 更新并验证
        int result = seatMapper.updateById(seat);
        assertEquals(1, result);
        
        // 查询并验证更新结果
        Seat updated = seatMapper.selectById(seat.getId());
        assertNotNull(updated);
        assertEquals("A-2", updated.getSeatNumber());
        assertEquals(0, updated.getHasPower());
    }
    
    /**
     * 测试删除操作
     */
    @Test
    void testDelete() {
        // 先插入座位
        Seat seat = createSeat(1L, "A-1", 1, 1);
        seatMapper.insert(seat);
        
        // 删除并验证
        int result = seatMapper.deleteById(seat.getId());
        assertEquals(1, result);
        
        // 查询并验证已删除
        Seat deleted = seatMapper.selectById(seat.getId());
        assertNull(deleted);
    }
    
    /**
     * 测试批量插入操作
     */
    @Test
    void testBatchInsert() {
        // 创建多个座位
        Seat seat1 = createSeat(1L, "A-1", 1, 1);
        Seat seat2 = createSeat(1L, "A-2", 1, 2);
        List<Seat> seats = Arrays.asList(seat1, seat2);
        
        // 批量插入并验证
        int result = seatMapper.batchInsertSeats(seats);
        assertEquals(2, result);
        
        // 查询并验证数量
        LambdaQueryWrapper<Seat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Seat::getStudyRoomId, 1L);
        List<Seat> selected = seatMapper.selectList(queryWrapper);
        assertEquals(2, selected.size());
    }
    
    /**
     * 测试更新座位状态操作
     */
    @Test
    void testUpdateSeatStatus() {
        // 先插入座位
        Seat seat = createSeat(1L, "A-1", 1, 1);
        seatMapper.insert(seat);
        
        // 更新状态并验证
        int result = seatMapper.updateSeatStatus(seat.getId(), 0);
        assertEquals(1, result);
        
        // 查询并验证状态已更新
        Seat updated = seatMapper.selectById(seat.getId());
        assertNotNull(updated);
        assertEquals(0, updated.getStatus());
    }
    
    /**
     * 测试高级查询功能（按条件查询）
     */
    @Test
    void testQuerySeatsByCondition() {
        // 先插入测试自习室（需要自习室存在才能关联查询）
        // 由于这个测试依赖于StudyRoomMapper，如果没有配置，可能需要先手动插入自习室数据
        
        // 插入不同特性的座位
        Seat seat1 = createSeat(1L, "A-1", 1, 1);
        seat1.setHasPower(1);
        seat1.setIsWindow(1);
        seat1.setIsCorner(1);
        
        Seat seat2 = createSeat(1L, "A-2", 1, 2);
        seat2.setHasPower(1);
        seat2.setIsWindow(0);
        seat2.setIsCorner(0);
        
        Seat seat3 = createSeat(1L, "A-3", 1, 3);
        seat3.setHasPower(0);
        seat3.setIsWindow(1);
        seat3.setIsCorner(0);
        
        seatMapper.insert(seat1);
        seatMapper.insert(seat2);
        seatMapper.insert(seat3);
        
        // 创建查询条件（有电源）
        SeatQueryDTO query = new SeatQueryDTO();
        query.setStudyRoomId(1L);
        query.setHasPower(1);
        
        // 执行条件查询
        Page<SeatVO> page = new Page<>(1, 10);
        IPage<SeatVO> result = seatMapper.querySeatsByCondition(page, query);
        
        // 验证查询结果（应该有2个座位有电源）
        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
    }
    
    /**
     * 测试获取座位详情功能
     */
    @Test
    void testGetSeatVOById() {
        // 先插入座位
        Seat seat = createSeat(1L, "A-1", 1, 1);
        seat.setHasPower(1);
        seat.setIsWindow(1);
        seat.setIsCorner(1);
        seatMapper.insert(seat);
        
        // 获取座位详情
        SeatVO seatVO = seatMapper.getSeatVOById(seat.getId());
        
        // 验证结果
        assertNotNull(seatVO);
        assertEquals(seat.getId(), seatVO.getId());
        assertEquals(seat.getSeatNumber(), seatVO.getSeatNumber());
        assertEquals("有", seatVO.getHasPowerText());
        assertEquals("是", seatVO.getIsWindowText());
        assertEquals("是", seatVO.getIsCornerText());
    }
    
    /**
     * 创建测试用的座位对象
     */
    private Seat createSeat(Long studyRoomId, String seatNumber, Integer row, Integer column) {
        Seat seat = new Seat();
        seat.setStudyRoomId(studyRoomId);
        seat.setSeatNumber(seatNumber);
        seat.setRowNumber(row);
        seat.setColumnNumber(column);
        seat.setHasPower(0);
        seat.setIsWindow(0);
        seat.setIsCorner(0);
        seat.setStatus(1);
        seat.setCreateTime(LocalDateTime.now());
        seat.setUpdateTime(LocalDateTime.now());
        return seat;
    }
} 