package com.studyseat.reserve.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
    @Select({
        "<script>",
        "SELECT sr.*, u.real_name AS admin_name, ",
        "(SELECT COUNT(*) FROM seat s WHERE s.study_room_id = sr.id AND s.status = 1) AS capacity, ",
        "(SELECT COUNT(*) FROM reservation r JOIN seat s ON r.seat_id = s.id ",
        "WHERE s.study_room_id = sr.id AND r.status IN (1, 2) ",
        "AND NOW() BETWEEN r.start_time AND r.end_time) AS used_capacity ",
        "FROM study_room sr ",
        "LEFT JOIN admin a ON sr.admin_id = a.id ",
        "LEFT JOIN user u ON a.user_id = u.id ",
        "WHERE 1 = 1 ",
        "<if test='name != null and name != \"\"'>",
        "AND sr.name LIKE CONCAT('%', #{name}, '%') ",
        "</if>",
        "<if test='building != null and building != \"\"'>",
        "AND sr.building = #{building} ",
        "</if>",
        "<if test='belongsTo != null and belongsTo != \"\"'>",
        "AND sr.belongs_to = #{belongsTo} ",
        "</if>",
        "<if test='isActive != null'>",
        "AND sr.is_active = #{isActive} ",
        "</if>",
        "AND sr.is_deleted = 0 ",
        "ORDER BY sr.create_time DESC",
        "</script>"
    })
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
    @Select({
        "SELECT sr.*, u.real_name AS admin_name, ",
        "(SELECT COUNT(*) FROM seat s WHERE s.study_room_id = sr.id AND s.status = 1) AS capacity, ",
        "(SELECT COUNT(*) FROM reservation r JOIN seat s ON r.seat_id = s.id ",
        "WHERE s.study_room_id = sr.id AND r.status IN (1, 2) ",
        "AND NOW() BETWEEN r.start_time AND r.end_time) AS used_capacity ",
        "FROM study_room sr ",
        "LEFT JOIN admin a ON sr.admin_id = a.id ",
        "LEFT JOIN user u ON a.user_id = u.id ",
        "WHERE sr.id = #{id} AND sr.is_deleted = 0"
    })
    StudyRoomVO selectStudyRoomById(@Param("id") Long id);
} 