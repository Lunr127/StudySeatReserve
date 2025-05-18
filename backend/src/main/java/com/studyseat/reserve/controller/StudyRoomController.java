package com.studyseat.reserve.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.studyseat.reserve.common.Result;
import com.studyseat.reserve.dto.StudyRoomDTO;
import com.studyseat.reserve.service.StudyRoomService;
import com.studyseat.reserve.util.SecurityUtil;
import com.studyseat.reserve.vo.StudyRoomVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 自习室控制器
 */
@Api(tags = "自习室管理")
@RestController
@RequestMapping("/api/study-rooms")
public class StudyRoomController {

    @Autowired
    private StudyRoomService studyRoomService;
    
    /**
     * 创建自习室
     */
    @ApiOperation(value = "创建自习室", notes = "仅管理员可用")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Long> createStudyRoom(@Valid @RequestBody StudyRoomDTO studyRoomDTO) {
        Long id = studyRoomService.createStudyRoom(studyRoomDTO);
        return Result.success(id).message("创建自习室成功");
    }
    
    /**
     * 更新自习室
     */
    @ApiOperation(value = "更新自习室", notes = "仅管理员可用")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> updateStudyRoom(
            @ApiParam(value = "自习室ID", required = true) @PathVariable Long id, 
            @Valid @RequestBody StudyRoomDTO studyRoomDTO) {
        studyRoomDTO.setId(id);
        boolean result = studyRoomService.updateStudyRoom(studyRoomDTO);
        return Result.success(result).message("更新自习室成功");
    }
    
    /**
     * 删除自习室
     */
    @ApiOperation(value = "删除自习室", notes = "仅管理员可用")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> deleteStudyRoom(
            @ApiParam(value = "自习室ID", required = true) @PathVariable Long id) {
        boolean result = studyRoomService.deleteStudyRoom(id);
        return Result.success(result).message("删除自习室成功");
    }
    
    /**
     * 获取自习室详情
     */
    @ApiOperation(value = "获取自习室详情")
    @GetMapping("/{id}")
    public Result<StudyRoomVO> getStudyRoomById(
            @ApiParam(value = "自习室ID", required = true) @PathVariable Long id) {
        // 检查权限
        Long userId = SecurityUtil.getCurrentUserId();
        boolean hasPermission = studyRoomService.checkPermission(id, userId);
        if (!hasPermission) {
            return Result.failure("无权访问该自习室");
        }
        
        StudyRoomVO studyRoomVO = studyRoomService.getStudyRoomById(id);
        return Result.success(studyRoomVO);
    }
    
    /**
     * 分页查询自习室
     */
    @ApiOperation(value = "分页查询自习室")
    @GetMapping
    public Result<IPage<StudyRoomVO>> pageStudyRoom(
            @ApiParam(value = "当前页", required = true) @RequestParam(defaultValue = "1") long current,
            @ApiParam(value = "每页大小", required = true) @RequestParam(defaultValue = "10") long size,
            @ApiParam(value = "自习室名称") @RequestParam(required = false) String name,
            @ApiParam(value = "所在建筑") @RequestParam(required = false) String building,
            @ApiParam(value = "归属") @RequestParam(required = false) String belongsTo,
            @ApiParam(value = "是否开放") @RequestParam(required = false) String isActive) {
        // 检查当前用户类型，如果是学生，需要过滤只能访问的自习室
        // 管理员可以查看所有自习室
        
        // 处理isActive参数，转换为Integer或null
        Integer activeStatus = null;
        if (isActive != null && !isActive.equals("null") && !isActive.equals("-1") && !isActive.trim().isEmpty()) {
            try {
                activeStatus = Integer.parseInt(isActive);
            } catch (NumberFormatException e) {
                // 如果转换失败，保持为null
            }
        }
        
        IPage<StudyRoomVO> pageResult = studyRoomService.pageStudyRoom(current, size, name, building, belongsTo, activeStatus);
        return Result.success(pageResult);
    }
    
    /**
     * 更新自习室状态
     */
    @ApiOperation(value = "更新自习室状态", notes = "仅管理员可用")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> updateStatus(
            @ApiParam(value = "自习室ID", required = true) @PathVariable Long id,
            @ApiParam(value = "状态：0-关闭，1-开放", required = true) @RequestParam Integer isActive) {
        boolean result = studyRoomService.updateStatus(id, isActive);
        return Result.success(result).message("更新自习室状态成功");
    }
} 