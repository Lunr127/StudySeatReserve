package com.studyseat.reserve.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyseat.reserve.entity.Seat;

/**
 * 座位数据访问层
 */
@Mapper
public interface SeatMapper extends BaseMapper<Seat> {

} 