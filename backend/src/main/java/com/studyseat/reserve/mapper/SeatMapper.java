package com.studyseat.reserve.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.dto.SeatQueryDTO;
import com.studyseat.reserve.entity.Seat;
import com.studyseat.reserve.vo.SeatVO;

/**
 * 座位数据访问层
 */
@Mapper
public interface SeatMapper extends BaseMapper<Seat> {
    
    /**
     * 高级查询座位
     * 
     * @param page 分页参数
     * @param query 查询条件
     * @return 座位视图对象分页列表
     */
    IPage<SeatVO> querySeatsByCondition(Page<SeatVO> page, @Param("query") SeatQueryDTO query);
    
    /**
     * 获取座位详情
     * 
     * @param id 座位ID
     * @return 座位视图对象
     */
    SeatVO getSeatVOById(@Param("id") Long id);
    
    /**
     * 批量添加座位
     * 
     * @param seats 座位列表
     * @return 影响的行数
     */
    int batchInsertSeats(@Param("seats") List<Seat> seats);
    
    /**
     * 更新座位状态
     * 
     * @param id 座位ID
     * @param status 状态
     * @return 影响的行数
     */
    int updateSeatStatus(@Param("id") Long id, @Param("status") Integer status);
} 