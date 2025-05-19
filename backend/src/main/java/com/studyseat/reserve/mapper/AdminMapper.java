package com.studyseat.reserve.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyseat.reserve.entity.Admin;
import com.studyseat.reserve.vo.AdminVO;

/**
 * 管理员Mapper接口
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    
    /**
     * 查询管理员列表
     * @return 管理员列表
     */
    @Select("SELECT a.id, a.user_id, a.admin_type, a.department, u.real_name " +
            "FROM admin a " +
            "LEFT JOIN user u ON a.user_id = u.id " +
            "WHERE a.is_deleted = 0 AND u.is_deleted = 0")
    List<AdminVO> selectAdminList();
} 