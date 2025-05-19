package com.studyseat.reserve.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.studyseat.reserve.dto.SeatDTO;
import com.studyseat.reserve.dto.SeatQueryDTO;
import com.studyseat.reserve.entity.Seat;
import com.studyseat.reserve.vo.SeatVO;

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
    
    /**
     * 高级查询座位
     * 
     * @param page 分页参数
     * @param query 查询条件
     * @return 座位视图对象分页列表
     */
    IPage<SeatVO> querySeatsByCondition(Page<SeatVO> page, SeatQueryDTO query);
    
    /**
     * 获取座位详情
     * 
     * @param id 座位ID
     * @return 座位视图对象
     */
    SeatVO getSeatVOById(Long id);
    
    /**
     * 添加座位
     * 
     * @param seatDTO 座位数据
     * @return 是否成功
     */
    boolean addSeat(SeatDTO seatDTO);
    
    /**
     * 批量添加座位
     * 
     * @param seatDTOs 座位数据列表
     * @return 是否成功
     */
    boolean batchAddSeats(List<SeatDTO> seatDTOs);
    
    /**
     * 生成座位（根据行列数自动生成）
     * 
     * @param studyRoomId 自习室ID
     * @param rows 行数
     * @param columns 列数
     * @param hasPower 是否有电源
     * @return 是否成功
     */
    boolean generateSeats(Long studyRoomId, Integer rows, Integer columns, Integer hasPower);
    
    /**
     * 更新座位
     * 
     * @param seatDTO 座位数据
     * @return 是否成功
     */
    boolean updateSeat(SeatDTO seatDTO);
    
    /**
     * 更新座位状态
     * 
     * @param id 座位ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateSeatStatus(Long id, Integer status);
    
    /**
     * 删除座位
     * 
     * @param id 座位ID
     * @return 是否成功
     */
    boolean deleteSeat(Long id);
    
    /**
     * 删除自习室下所有座位
     * 
     * @param studyRoomId 自习室ID
     * @return 是否成功
     */
    boolean deleteAllSeatsInRoom(Long studyRoomId);
} 