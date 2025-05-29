package com.studyseat.reserve.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.dto.StudyRoomDTO;
import com.studyseat.reserve.entity.Admin;
import com.studyseat.reserve.entity.StudyRoom;
import com.studyseat.reserve.entity.User;
import com.studyseat.reserve.mapper.AdminMapper;
import com.studyseat.reserve.mapper.StudyRoomMapper;
import com.studyseat.reserve.mapper.UserMapper;
import com.studyseat.reserve.service.impl.StudyRoomServiceImpl;
import com.studyseat.reserve.vo.StudyRoomVO;

@ExtendWith(MockitoExtension.class)
public class StudyRoomServiceTest {

    @Mock
    private StudyRoomMapper studyRoomMapper;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private AdminMapper adminMapper;
    
    @InjectMocks
    private StudyRoomServiceImpl studyRoomService;
    
    private StudyRoomDTO studyRoomDTO;
    private StudyRoom studyRoom;
    private StudyRoomVO studyRoomVO;
    private User adminUser;
    private User studentUser;
    private Admin admin;

    @BeforeEach
    void setUp() {
        // 设置自习室DTO
        studyRoomDTO = new StudyRoomDTO();
        studyRoomDTO.setId(1L);
        studyRoomDTO.setName("测试自习室");
        studyRoomDTO.setLocation("A栋");
        studyRoomDTO.setBuilding("教学楼");
        studyRoomDTO.setFloor("2");
        studyRoomDTO.setRoomNumber("A201");
        studyRoomDTO.setCapacity(50);
        studyRoomDTO.setDescription("这是一个测试自习室");
        studyRoomDTO.setOpenTime(LocalTime.of(8, 0));
        studyRoomDTO.setCloseTime(LocalTime.of(22, 0));
        studyRoomDTO.setBelongsTo("全校");
        studyRoomDTO.setIsActive(1);
        studyRoomDTO.setAdminId(1L);
        
        // 设置自习室实体
        studyRoom = new StudyRoom();
        studyRoom.setId(1L);
        studyRoom.setName("测试自习室");
        studyRoom.setLocation("A栋");
        studyRoom.setBuilding("教学楼");
        studyRoom.setFloor("2");
        studyRoom.setRoomNumber("A201");
        studyRoom.setCapacity(50);
        studyRoom.setDescription("这是一个测试自习室");
        studyRoom.setOpenTime(LocalTime.of(8, 0));
        studyRoom.setCloseTime(LocalTime.of(22, 0));
        studyRoom.setBelongsTo("全校");
        studyRoom.setIsActive(1);
        studyRoom.setAdminId(1L);
        
        // 设置自习室VO
        studyRoomVO = new StudyRoomVO();
        studyRoomVO.setId(1L);
        studyRoomVO.setName("测试自习室");
        studyRoomVO.setLocation("A栋");
        studyRoomVO.setBuilding("教学楼");
        studyRoomVO.setFloor("2");
        studyRoomVO.setRoomNumber("A201");
        studyRoomVO.setCapacity(50);
        studyRoomVO.setDescription("这是一个测试自习室");
        studyRoomVO.setOpenTime(LocalTime.of(8, 0));
        studyRoomVO.setCloseTime(LocalTime.of(22, 0));
        studyRoomVO.setBelongsTo("全校");
        studyRoomVO.setIsActive(1);
        studyRoomVO.setAdminId(1L);
        studyRoomVO.setAdminName("管理员");
        studyRoomVO.setUsedCapacity(10);
        
        // 设置管理员用户
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setUserType(1);
        
        // 设置学生用户
        studentUser = new User();
        studentUser.setId(2L);
        studentUser.setUsername("student");
        studentUser.setUserType(2);
        
        // 设置管理员信息
        admin = new Admin();
        admin.setId(1L);
        admin.setUserId(1L);
        admin.setDepartment("计算机学院");
    }

    @Test
    void testCreateStudyRoom() {
        // 模拟插入
        when(studyRoomMapper.insert(any(StudyRoom.class))).thenReturn(1);
        
        // 执行创建自习室
        Long id = studyRoomService.createStudyRoom(studyRoomDTO);
        
        // 验证结果
        assertNotNull(id);
        
        // 验证调用
        verify(studyRoomMapper).insert(any(StudyRoom.class));
    }

    @Test
    void testUpdateStudyRoom() {
        // 模拟查询
        when(studyRoomMapper.selectById(anyLong())).thenReturn(studyRoom);
        
        // 模拟更新
        when(studyRoomMapper.updateById(any(StudyRoom.class))).thenReturn(1);
        
        // 执行更新自习室
        boolean result = studyRoomService.updateStudyRoom(studyRoomDTO);
        
        // 验证结果
        assertTrue(result);
        
        // 验证调用
        verify(studyRoomMapper).selectById(anyLong());
        verify(studyRoomMapper).updateById(any(StudyRoom.class));
    }

    @Test
    void testDeleteStudyRoom() {
        // 模拟删除
        when(studyRoomMapper.deleteById(anyLong())).thenReturn(1);
        
        // 执行删除自习室
        boolean result = studyRoomService.deleteStudyRoom(1L);
        
        // 验证结果
        assertTrue(result);
        
        // 验证调用
        verify(studyRoomMapper).deleteById(anyLong());
    }

    @Test
    void testGetStudyRoomById() {
        // 模拟查询
        when(studyRoomMapper.selectStudyRoomById(anyLong())).thenReturn(studyRoomVO);
        
        // 执行获取自习室
        StudyRoomVO result = studyRoomService.getStudyRoomById(1L);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(studyRoomVO.getId(), result.getId());
        assertEquals(studyRoomVO.getName(), result.getName());
        
        // 验证调用
        verify(studyRoomMapper).selectStudyRoomById(anyLong());
    }

    @Test
    void testPageStudyRoom() {
        // 创建分页对象
        Page<StudyRoomVO> page = new Page<>(1, 10);
        page.setRecords(List.of(studyRoomVO));
        page.setTotal(1);
        
        // 模拟分页查询
        when(studyRoomMapper.selectStudyRoomPage(any(), anyString(), anyString(), anyString(), anyInt()))
            .thenReturn(page);
        
        // 执行分页查询
        IPage<StudyRoomVO> result = studyRoomService.pageStudyRoom(1, 10, "测试", "教学楼", "全校", 1);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(studyRoomVO.getId(), result.getRecords().get(0).getId());
        
        // 验证调用
        verify(studyRoomMapper).selectStudyRoomPage(any(), anyString(), anyString(), anyString(), anyInt());
    }

    @Test
    void testUpdateStatus() {
        // 模拟查询
        when(studyRoomMapper.selectById(anyLong())).thenReturn(studyRoom);
        
        // 模拟更新
        when(studyRoomMapper.updateById(any(StudyRoom.class))).thenReturn(1);
        
        // 执行更新状态
        boolean result = studyRoomService.updateStatus(1L, 0);
        
        // 验证结果
        assertTrue(result);
        
        // 验证调用
        verify(studyRoomMapper).selectById(anyLong());
        verify(studyRoomMapper).updateById(any(StudyRoom.class));
    }

    @Test
    void testCheckPermissionAdmin() {
        // 模拟获取用户
        when(userMapper.selectById(anyLong())).thenReturn(adminUser);
        
        // 执行权限检查
        boolean result = studyRoomService.checkPermission(1L, adminUser.getId());
        
        // 验证结果（管理员应该有权限）
        assertTrue(result);
        
        // 验证调用
        verify(userMapper).selectById(anyLong());
    }

    @Test
    void testCheckPermissionStudentForAllSchool() {
        // 模拟获取用户
        when(userMapper.selectById(anyLong())).thenReturn(studentUser);
        
        // 模拟查询自习室
        when(studyRoomMapper.selectById(anyLong())).thenReturn(studyRoom);
        
        // 执行权限检查（自习室归属为"全校"）
        boolean result = studyRoomService.checkPermission(1L, studentUser.getId());
        
        // 验证结果（学生应该有权限访问全校自习室）
        assertTrue(result);
        
        // 验证调用
        verify(userMapper).selectById(anyLong());
        verify(studyRoomMapper).selectById(anyLong());
    }

    @Test
    void testCheckPermissionStudentForDepartment() {
        // 模拟获取用户
        when(userMapper.selectById(anyLong())).thenReturn(studentUser);
        
        // 修改自习室归属为特定院系
        studyRoom.setBelongsTo("计算机学院");
        
        // 模拟查询自习室
        when(studyRoomMapper.selectById(anyLong())).thenReturn(studyRoom);
        
        // 模拟学生所属院系查询（不属于该院系）
        when(adminMapper.selectOne(any())).thenReturn(null);
        
        // 执行权限检查
        boolean result = studyRoomService.checkPermission(1L, studentUser.getId());
        
        // 验证结果（学生应该没有权限访问非本院系自习室）
        assertFalse(result);
        
        // 验证调用
        verify(userMapper).selectById(anyLong());
        verify(studyRoomMapper).selectById(anyLong());
        verify(adminMapper).selectOne(any());
    }
} 