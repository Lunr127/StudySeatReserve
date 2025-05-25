package com.studyseat.reserve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.entity.CheckCode;
import com.studyseat.reserve.vo.CheckCodeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

/**
 * 签到码Mapper接口
 */
@Mapper
public interface CheckCodeMapper extends BaseMapper<CheckCode> {
    
    /**
     * 分页查询签到码（带详细信息）
     * 
     * @param page 分页参数
     * @param studyRoomId 自习室ID（可选）
     * @param validDate 有效日期（可选）
     * @return 签到码分页
     */
    IPage<CheckCodeVO> selectCheckCodePage(Page<CheckCodeVO> page,
                                           @Param("studyRoomId") Long studyRoomId,
                                           @Param("validDate") LocalDate validDate);
    
    /**
     * 根据自习室ID和日期查询签到码
     * 
     * @param studyRoomId 自习室ID
     * @param validDate 有效日期
     * @return 签到码
     */
    CheckCode selectByRoomIdAndDate(@Param("studyRoomId") Long studyRoomId,
                                    @Param("validDate") LocalDate validDate);
    
    /**
     * 根据签到码查询详细信息
     * 
     * @param code 签到码
     * @param validDate 有效日期
     * @return 签到码详情
     */
    CheckCodeVO selectByCodeAndDate(@Param("code") String code,
                                    @Param("validDate") LocalDate validDate);
} 