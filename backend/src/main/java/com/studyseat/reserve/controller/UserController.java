package com.studyseat.reserve.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studyseat.reserve.common.Result;
import com.studyseat.reserve.common.ResultCode;
import com.studyseat.reserve.dto.UserPreferenceDTO;
import com.studyseat.reserve.entity.Student;
import com.studyseat.reserve.entity.User;
import com.studyseat.reserve.exception.BusinessException;
import com.studyseat.reserve.mapper.StudentMapper;
import com.studyseat.reserve.mapper.UserMapper;
import com.studyseat.reserve.service.UserPreferenceService;
import com.studyseat.reserve.service.ViolationService;
import com.studyseat.reserve.vo.UserInfoVO;
import com.studyseat.reserve.vo.UserPreferenceVO;
import com.studyseat.reserve.vo.ViolationVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Api(tags = "用户接口")
public class UserController {

    private final UserMapper userMapper;
    private final StudentMapper studentMapper;
    private final ViolationService violationService;
    private final UserPreferenceService userPreferenceService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    @ApiOperation("获取当前用户信息")
    public Result<UserInfoVO> getCurrentUserInfo() {
        // 获取当前登录用户名
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // 查询用户信息
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 转换为VO对象
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(user.getId());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setRealName(user.getRealName());
        userInfoVO.setUserType(user.getUserType());
        userInfoVO.setPhone(user.getPhone());
        userInfoVO.setEmail(user.getEmail());
        userInfoVO.setAvatar(user.getAvatar());
        userInfoVO.setStatus(user.getStatus());
        
        return Result.success(userInfoVO);
    }

    /**
     * 获取用户违约记录
     */
    @GetMapping("/violations")
    @ApiOperation("获取用户违约记录")
    public Result<IPage<ViolationVO>> getUserViolations(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        // 获取当前用户的学生ID
        Long studentId = getCurrentStudentId();
        
        // 分页查询违约记录
        Page<ViolationVO> page = new Page<>(current, size);
        IPage<ViolationVO> violationPage = violationService.getViolationPageByStudentId(studentId, page);
        
        return Result.success(violationPage);
    }

    /**
     * 获取用户违约统计
     */
    @GetMapping("/violation-count")
    @ApiOperation("获取用户违约统计")
    public Result<Integer> getUserViolationCount() {
        // 获取当前用户的学生ID
        Long studentId = getCurrentStudentId();
        
        // 查询违约总数
        Integer count = violationService.getViolationCountByStudentId(studentId);
        
        return Result.success(count);
    }

    /**
     * 获取用户偏好设置
     */
    @GetMapping("/preferences")
    @ApiOperation("获取用户偏好设置")
    public Result<UserPreferenceVO> getUserPreferences() {
        // 获取当前用户ID
        Long userId = getCurrentUserId();
        
        // 查询用户偏好设置
        UserPreferenceVO preferences = userPreferenceService.getUserPreference(userId);
        
        return Result.success(preferences);
    }

    /**
     * 更新用户偏好设置
     */
    @PutMapping("/preferences")
    @ApiOperation("更新用户偏好设置")
    public Result<Void> updateUserPreferences(@RequestBody UserPreferenceDTO preferenceDTO) {
        // 获取当前用户ID
        Long userId = getCurrentUserId();
        
        // 更新用户偏好设置
        boolean success = userPreferenceService.updateUserPreference(userId, preferenceDTO);
        
        if (success) {
            return Result.success();
        } else {
            return Result.error("更新偏好设置失败");
        }
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        return user.getId();
    }

    /**
     * 获取当前学生ID
     */
    private Long getCurrentStudentId() {
        Long userId = getCurrentUserId();
        
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getUserId, userId);
        Student student = studentMapper.selectOne(wrapper);
        
        if (student == null) {
            throw new BusinessException("当前用户不是学生");
        }
        
        return student.getId();
    }
} 