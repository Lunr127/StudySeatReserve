package com.studyseat.reserve.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studyseat.reserve.dto.LoginRequestDTO;
import com.studyseat.reserve.dto.LoginResponseDTO;
import com.studyseat.reserve.dto.WxLoginRequestDTO;
import com.studyseat.reserve.dto.WxLoginRequestDTO.WxUserInfo;
import com.studyseat.reserve.entity.User;
import com.studyseat.reserve.exception.BusinessException;
import com.studyseat.reserve.mapper.UserMapper;
import com.studyseat.reserve.service.impl.AuthServiceImpl;
import com.studyseat.reserve.util.JwtUtil;
import com.studyseat.reserve.util.WxApiUtil;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private WxApiUtil wxApiUtil;
    
    @Mock
    private Authentication authentication;
    
    @InjectMocks
    private AuthServiceImpl authService;
    
    private LoginRequestDTO loginRequest;
    private User user;
    private WxLoginRequestDTO wxLoginRequest;
    private WxUserInfo wxUserInfo;

    @BeforeEach
    void setUp() {
        // 设置常规登录请求数据
        loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");
        
        // 设置用户数据
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRealName("Test User");
        user.setUserType(2);
        user.setStatus(1);
        user.setAvatar("avatar.jpg");
        
        // 设置微信登录请求数据
        wxLoginRequest = new WxLoginRequestDTO();
        wxLoginRequest.setCode("wx_code");
        
        wxUserInfo = new WxUserInfo();
        wxUserInfo.setNickName("WxUser");
        wxUserInfo.setAvatarUrl("wx_avatar.jpg");
        wxLoginRequest.setUserInfo(wxUserInfo);
    }

    @Test
    void testLoginSuccess() {
        // 模拟认证成功
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        
        // 模拟用户查询
        when(userMapper.selectOne(any())).thenReturn(user);
        
        // 模拟JWT生成
        when(jwtUtil.generateToken(anyString(), anyLong(), anyInt())).thenReturn("test.jwt.token");
        
        // 执行登录
        LoginResponseDTO response = authService.login(loginRequest);
        
        // 验证结果
        assertNotNull(response);
        assertEquals("test.jwt.token", response.getToken());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getRealName(), response.getRealName());
        assertEquals(user.getUserType(), response.getUserType());
        assertEquals(user.getAvatar(), response.getAvatar());
        
        // 验证调用
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userMapper).selectOne(any());
        verify(jwtUtil).generateToken(anyString(), anyLong(), anyInt());
    }

    @Test
    void testLoginFailBadCredentials() {
        // 模拟认证失败
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));
        
        // 执行登录并验证异常
        assertThrows(BusinessException.class, () -> authService.login(loginRequest));
        
        // 验证调用
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userMapper, never()).selectOne(any());
        verify(jwtUtil, never()).generateToken(anyString(), anyLong(), anyInt());
    }

    @Test
    void testLoginFailUserNotFound() {
        // 模拟认证成功
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        
        // 模拟用户查询（未找到用户）
        when(userMapper.selectOne(any())).thenReturn(null);
        
        // 执行登录并验证异常
        assertThrows(BusinessException.class, () -> authService.login(loginRequest));
        
        // 验证调用
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userMapper).selectOne(any());
        verify(jwtUtil, never()).generateToken(anyString(), anyLong(), anyInt());
    }

    @Test
    void testWxLoginExistingUser() {
        // 模拟获取OpenID
        String openId = "test_open_id";
        when(wxApiUtil.getOpenId(anyString())).thenReturn(openId);
        
        // 模拟用户查询（已存在用户）
        user.setOpenId(openId);
        when(userMapper.selectOne(any())).thenReturn(user);
        
        // 模拟JWT生成
        when(jwtUtil.generateToken(anyString(), anyLong(), anyInt())).thenReturn("test.jwt.token");
        
        // 执行微信登录
        LoginResponseDTO response = authService.wxLogin(wxLoginRequest);
        
        // 验证结果
        assertNotNull(response);
        assertEquals("test.jwt.token", response.getToken());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getRealName(), response.getRealName());
        assertEquals(user.getUserType(), response.getUserType());
        assertEquals(user.getAvatar(), response.getAvatar());
        
        // 验证调用
        verify(wxApiUtil).getOpenId(anyString());
        verify(userMapper).selectOne(any());
        verify(userMapper, never()).insert(any());
        verify(jwtUtil).generateToken(anyString(), anyLong(), anyInt());
    }

    @Test
    void testWxLoginNewUser() {
        // 模拟获取OpenID
        String openId = "test_open_id";
        when(wxApiUtil.getOpenId(anyString())).thenReturn(openId);
        
        // 模拟用户查询（不存在用户）
        when(userMapper.selectOne(any())).thenReturn(null);
        
        // 模拟用户插入
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return 1;
        }).when(userMapper).insert(any(User.class));
        
        // 模拟JWT生成
        when(jwtUtil.generateToken(anyString(), anyLong(), anyInt())).thenReturn("test.jwt.token");
        
        // 执行微信登录
        LoginResponseDTO response = authService.wxLogin(wxLoginRequest);
        
        // 验证结果
        assertNotNull(response);
        assertEquals("test.jwt.token", response.getToken());
        assertEquals(1L, response.getUserId());
        assertTrue(response.getUsername().startsWith("wx_"));
        assertEquals(wxUserInfo.getNickName(), response.getRealName());
        assertEquals(2, response.getUserType());
        assertEquals(wxUserInfo.getAvatarUrl(), response.getAvatar());
        
        // 验证调用
        verify(wxApiUtil).getOpenId(anyString());
        verify(userMapper).selectOne(any());
        verify(userMapper).insert(any(User.class));
        verify(jwtUtil).generateToken(anyString(), anyLong(), anyInt());
    }

    @Test
    void testWxLoginFailGetOpenId() {
        // 模拟获取OpenID失败
        when(wxApiUtil.getOpenId(anyString())).thenReturn(null);
        
        // 执行微信登录并验证异常
        assertThrows(BusinessException.class, () -> authService.wxLogin(wxLoginRequest));
        
        // 验证调用
        verify(wxApiUtil).getOpenId(anyString());
        verify(userMapper, never()).selectOne(any());
        verify(userMapper, never()).insert(any());
        verify(jwtUtil, never()).generateToken(anyString(), anyLong(), anyInt());
    }

    @Test
    void testLogout() {
        // 执行登出
        authService.logout("test.jwt.token");
        
        // 无需验证调用（因为方法体内容较少）
    }
} 