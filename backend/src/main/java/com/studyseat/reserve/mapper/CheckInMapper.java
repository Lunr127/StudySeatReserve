package com.studyseat.reserve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.entity.CheckIn;
import com.studyseat.reserve.vo.CheckInVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 签到Mapper接口
 */
@Mapper
public interface CheckInMapper extends BaseMapper<CheckIn> {
    
    /**
     * 分页查询签到记录（带详细信息）
     * 
     * @param page 分页参数
     * @param studyRoomId 自习室ID（可选）
     * @param studentId 学生ID（可选）
     * @return 签到记录分页
     */
    IPage<CheckInVO> selectCheckInPage(Page<CheckInVO> page, 
                                       @Param("studyRoomId") Long studyRoomId,
                                       @Param("studentId") Long studentId);
    
    /**
     * 根据预约ID查询签到信息
     * 
     * @param reservationId 预约ID
     * @return 签到信息
     */
    CheckInVO selectByReservationId(@Param("reservationId") Long reservationId);
    
    /**
     * 根据ID查询签到详情
     * 
     * @param id 签到ID
     * @return 签到详情
     */
    CheckInVO selectCheckInById(@Param("id") Long id);
} 