package com.studyseat.reserve.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyseat.reserve.common.ResultCode;
import com.studyseat.reserve.dto.LoginRequestDTO;
import com.studyseat.reserve.dto.LoginResponseDTO;
import com.studyseat.reserve.dto.WxLoginRequestDTO;
import com.studyseat.reserve.entity.User;
import com.studyseat.reserve.exception.BusinessException;
import com.studyseat.reserve.mapper.UserMapper;
import com.studyseat.reserve.service.AuthService;
import com.studyseat.reserve.util.JwtUtil;
import com.studyseat.reserve.util.WxApiUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 认证服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final WxApiUtil wxApiUtil;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            // 进行身份验证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            
            // 认证通过，设置认证信息到上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 查询用户信息
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, loginRequest.getUsername());
            User user = userMapper.selectOne(wrapper);
            
            if (user == null) {
                throw new BusinessException(ResultCode.USER_NOT_FOUND);
            }
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getUserType());
            
            // 返回登录响应
            return LoginResponseDTO.builder()
                    .token(token)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .realName(user.getRealName())
                    .userType(user.getUserType())
                    .avatar(user.getAvatar())
                    .build();
        } catch (BadCredentialsException e) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }
    }

    @Override
    public LoginResponseDTO wxLogin(WxLoginRequestDTO wxLoginRequest) {
        try {
            // 调用微信API获取OpenID
            String openId = wxApiUtil.getOpenId(wxLoginRequest.getCode());
            
            if (openId == null) {
                throw new BusinessException(ResultCode.WX_LOGIN_ERROR, "获取OpenID失败");
            }
            
            // 根据OpenID查询用户
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getOpenId, openId);
            User user = userMapper.selectOne(wrapper);
            
            // 如果用户不存在，则创建新用户
            if (user == null) {
                user = new User();
                user.setOpenId(openId);
                user.setUsername("wx_" + openId.substring(0, 8));
                user.setUserType(2); // 学生类型
                user.setStatus(1); // 正常状态
                
                // 设置用户信息
                if (wxLoginRequest.getUserInfo() != null) {
                    user.setRealName(wxLoginRequest.getUserInfo().getNickName());
                    user.setAvatar(wxLoginRequest.getUserInfo().getAvatarUrl());
                }
                
                userMapper.insert(user);
            }
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getUserType());
            
            // 返回登录响应
            return LoginResponseDTO.builder()
                    .token(token)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .realName(user.getRealName())
                    .userType(user.getUserType())
                    .avatar(user.getAvatar())
                    .build();
        } catch (Exception e) {
            log.error("微信登录失败", e);
            throw new BusinessException(ResultCode.WX_LOGIN_ERROR, e.getMessage());
        }
    }

    @Override
    public void logout(String token) {
        // JWT是无状态的，服务端不需要做特殊处理
        // 如果需要实现Token黑名单，可以在这里添加处理逻辑
        SecurityContextHolder.clearContext();
    }
} 