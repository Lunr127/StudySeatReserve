package com.studyseat.reserve.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyseat.reserve.entity.Student;

/**
 * 学生Mapper接口
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    
    /**
     * 根据用户ID查询学生信息
     * 
     * @param userId 用户ID
     * @return 学生信息
     */
    @Select("SELECT * FROM student WHERE user_id = #{userId} AND is_deleted = 0")
    Student selectByUserId(@Param("userId") Long userId);
} 