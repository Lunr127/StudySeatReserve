package com.studyseat.reserve.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.dto.SeatDTO;
import com.studyseat.reserve.dto.SeatQueryDTO;
import com.studyseat.reserve.entity.Seat;
import com.studyseat.reserve.service.SeatService;
import com.studyseat.reserve.service.StudyRoomService;
import com.studyseat.reserve.util.SecurityUtil;
import com.studyseat.reserve.vo.SeatVO;

/**
 * 座位控制器集成测试类
 */
@WebMvcTest(SeatController.class)
public class SeatControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private SeatService seatService;
    
    @MockBean
    private StudyRoomService studyRoomService;
    
    @MockBean
    private SecurityUtil securityUtil;
    
    private SeatDTO seatDTO;
    private SeatVO seatVO;
    private List<SeatDTO> seatDTOs;
    
    @BeforeEach
    void setUp() {
        // 设置座位DTO
        seatDTO = new SeatDTO();
        seatDTO.setId(1L);
        seatDTO.setStudyRoomId(1L);
        seatDTO.setSeatNumber("A-1");
        seatDTO.setRowNumber(1);
        seatDTO.setColumnNumber(1);
        seatDTO.setHasPower(1);
        seatDTO.setIsWindow(1);
        seatDTO.setIsCorner(1);
        seatDTO.setStatus(1);
        
        // 设置座位VO
        seatVO = new SeatVO();
        seatVO.setId(1L);
        seatVO.setStudyRoomId(1L);
        seatVO.setStudyRoomName("测试自习室");
        seatVO.setSeatNumber("A-1");
        seatVO.setRowNumber(1);
        seatVO.setColumnNumber(1);
        seatVO.setHasPower(1);
        seatVO.setHasPowerText("有");
        seatVO.setIsWindow(1);
        seatVO.setIsWindowText("是");
        seatVO.setIsCorner(1);
        seatVO.setIsCornerText("是");
        seatVO.setStatus(1);
        seatVO.setStatusText("正常");
        
        // 设置批量座位DTO列表
        seatDTOs = List.of(seatDTO);
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddSeat() throws Exception {
        when(studyRoomService.checkPermission(anyLong(), anyLong())).thenReturn(true);
        when(seatService.addSeat(any(SeatDTO.class))).thenReturn(true);
        
        mockMvc.perform(post("/api/seats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(seatDTO))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true));
        
        verify(seatService).addSeat(any(SeatDTO.class));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testBatchAddSeats() throws Exception {
        when(studyRoomService.checkPermission(anyLong(), anyLong())).thenReturn(true);
        when(seatService.batchAddSeats(anyList())).thenReturn(true);
        
        mockMvc.perform(post("/api/seats/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(seatDTOs))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true));
        
        verify(seatService).batchAddSeats(anyList());
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGenerateSeats() throws Exception {
        when(studyRoomService.checkPermission(anyLong(), anyLong())).thenReturn(true);
        when(seatService.generateSeats(anyLong(), anyInt(), anyInt(), anyInt())).thenReturn(true);
        
        mockMvc.perform(post("/api/seats/generate")
                .param("studyRoomId", "1")
                .param("rows", "3")
                .param("columns", "4")
                .param("hasPower", "1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true));
        
        verify(seatService).generateSeats(anyLong(), anyInt(), anyInt(), anyInt());
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateSeat() throws Exception {
        when(studyRoomService.checkPermission(anyLong(), anyLong())).thenReturn(true);
        when(seatService.getSeatVOById(anyLong())).thenReturn(seatVO);
        when(seatService.updateSeat(any(SeatDTO.class))).thenReturn(true);
        
        mockMvc.perform(put("/api/seats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(seatDTO))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true));
        
        verify(seatService).updateSeat(any(SeatDTO.class));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateSeatStatus() throws Exception {
        when(studyRoomService.checkPermission(anyLong(), anyLong())).thenReturn(true);
        when(seatService.getSeatVOById(anyLong())).thenReturn(seatVO);
        when(seatService.updateSeatStatus(anyLong(), anyInt())).thenReturn(true);
        
        mockMvc.perform(put("/api/seats/{id}/status/{status}", 1, 0)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true));
        
        verify(seatService).updateSeatStatus(anyLong(), anyInt());
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteSeat() throws Exception {
        when(studyRoomService.checkPermission(anyLong(), anyLong())).thenReturn(true);
        when(seatService.getSeatVOById(anyLong())).thenReturn(seatVO);
        when(seatService.deleteSeat(anyLong())).thenReturn(true);
        
        mockMvc.perform(delete("/api/seats/{id}", 1)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true));
        
        verify(seatService).deleteSeat(anyLong());
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteAllSeatsInRoom() throws Exception {
        when(studyRoomService.checkPermission(anyLong(), anyLong())).thenReturn(true);
        when(seatService.deleteAllSeatsInRoom(anyLong())).thenReturn(true);
        
        mockMvc.perform(delete("/api/seats/room/{studyRoomId}", 1)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true));
        
        verify(seatService).deleteAllSeatsInRoom(anyLong());
    }
    
    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void testGetSeatById() throws Exception {
        when(studyRoomService.checkPermission(anyLong(), anyLong())).thenReturn(true);
        when(seatService.getSeatVOById(anyLong())).thenReturn(seatVO);
        
        mockMvc.perform(get("/api/seats/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.seatNumber").value("A-1"));
        
        verify(seatService).getSeatVOById(anyLong());
    }
    
    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void testGetSeatsByRoomId() throws Exception {
        Page<Seat> page = new Page<>(1, 10);
        page.setRecords(List.of(new Seat()));
        page.setTotal(1);
        
        when(studyRoomService.checkPermission(anyLong(), anyLong())).thenReturn(true);
        when(seatService.getSeatsByRoomId(any(), anyLong())).thenReturn(page);
        
        mockMvc.perform(get("/api/seats/room/{studyRoomId}", 1)
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records").isArray());
        
        verify(seatService).getSeatsByRoomId(any(), anyLong());
    }
    
    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void testQuerySeatsByCondition() throws Exception {
        Page<SeatVO> page = new Page<>(1, 10);
        page.setRecords(List.of(seatVO));
        page.setTotal(1);
        
        when(studyRoomService.checkPermission(anyLong(), anyLong())).thenReturn(true);
        when(seatService.querySeatsByCondition(any(), any(SeatQueryDTO.class))).thenReturn(page);
        
        mockMvc.perform(get("/api/seats/condition")
                .param("studyRoomId", "1")
                .param("hasPower", "1")
                .param("isWindow", "1")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].seatNumber").value("A-1"));
        
        verify(seatService).querySeatsByCondition(any(), any(SeatQueryDTO.class));
    }
    
    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void testNonPermissionAccess() throws Exception {
        when(studyRoomService.checkPermission(anyLong(), anyLong())).thenReturn(false);
        
        mockMvc.perform(get("/api/seats/{id}", 1))
                .andExpect(status().isForbidden());
        
        verify(seatService, never()).getSeatVOById(anyLong());
    }
} 