package com.studyseat.reserve.service;

import com.studyseat.reserve.dto.LoginRequestDTO;
import com.studyseat.reserve.dto.LoginResponseDTO;
import com.studyseat.reserve.dto.WxLoginRequestDTO;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户名密码登录
     * 
     * @param loginRequest 登录请求DTO
     * @return 登录响应DTO
     */
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    
    /**
     * 微信小程序登录
     * 
     * @param wxLoginRequest 微信登录请求DTO
     * @return 登录响应DTO
     */
    LoginResponseDTO wxLogin(WxLoginRequestDTO wxLoginRequest);
    
    /**
     * 用户退出登录
     * 
     * @param token JWT令牌
     */
    void logout(String token);
} 