package com.studyseat.reserve.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.entity.Violation;
import com.studyseat.reserve.vo.ViolationVO;

/**
 * 违约记录Mapper接口
 */
@Mapper
public interface ViolationMapper extends BaseMapper<Violation> {
    
    /**
     * 分页查询用户违约记录
     * @param page 分页对象
     * @param studentId 学生ID
     * @return 违约记录列表
     */
    IPage<ViolationVO> selectViolationPageByStudentId(Page<ViolationVO> page, @Param("studentId") Long studentId);
    
    /**
     * 获取用户违约总数
     * @param studentId 学生ID
     * @return 违约总数
     */
    Integer countViolationByStudentId(@Param("studentId") Long studentId);
} 