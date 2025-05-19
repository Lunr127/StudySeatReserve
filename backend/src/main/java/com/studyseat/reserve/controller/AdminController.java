package com.studyseat.reserve.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studyseat.reserve.common.Result;
import com.studyseat.reserve.entity.Admin;
import com.studyseat.reserve.mapper.AdminMapper;
import com.studyseat.reserve.vo.AdminVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admins")
public class AdminController {

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 获取管理员列表
     * @return 管理员列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<AdminVO>> getAdminList() {
        // 查询所有未删除的管理员
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getIsDeleted, 0);
        List<Admin> adminList = adminMapper.selectList(queryWrapper);
        
        // 转换为VO对象
        List<AdminVO> adminVOList = adminMapper.selectAdminList();
        
        return Result.success(adminVOList);
    }
} 