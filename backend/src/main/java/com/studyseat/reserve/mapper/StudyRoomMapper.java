package com.studyseat.reserve.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.entity.StudyRoom;
import com.studyseat.reserve.vo.StudyRoomVO;

/**
 * 自习室数据访问层
 */
@Mapper
public interface StudyRoomMapper extends BaseMapper<StudyRoom> {
    
    /**
     * 分页查询自习室列表（包含管理员姓名和已使用座位数）
     * 
     * @param page 分页参数
     * @param name 自习室名称（模糊搜索）
     * @param building 所在建筑
     * @param belongsTo 归属（全校/特定院系）
     * @param isActive 是否开放
     * @return 分页结果
     */
    IPage<StudyRoomVO> selectStudyRoomPage(Page<StudyRoomVO> page, 
                                          @Param("name") String name,
                                          @Param("building") String building,
                                          @Param("belongsTo") String belongsTo,
                                          @Param("isActive") Integer isActive);
    
    /**
     * 获取自习室详情（包含管理员姓名和已使用座位数）
     * 
     * @param id 自习室ID
     * @return 自习室详情
     */
    StudyRoomVO selectStudyRoomById(@Param("id") Long id);
} 