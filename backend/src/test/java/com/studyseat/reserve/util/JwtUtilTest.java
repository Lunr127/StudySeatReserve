package com.studyseat.reserve.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    private String testUsername = "testuser";
    private Long testUserId = 1L;
    private Integer testUserType = 2; // 学生

    @BeforeEach
    void setUp() {
        // 设置JWT工具类的属性值
        ReflectionTestUtils.setField(jwtUtil, "secret", "studyseat-reserve-jwt-secret-key");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600L); // 1小时
        ReflectionTestUtils.setField(jwtUtil, "tokenHeader", "Authorization");
        ReflectionTestUtils.setField(jwtUtil, "tokenHead", "Bearer");
    }

    @Test
    void testGenerateToken() {
        // 生成令牌
        String token = jwtUtil.generateToken(testUsername, testUserId, testUserType);
        
        // 验证令牌不为空
        assertNotNull(token);
        // 验证令牌长度大于20
        assertTrue(token.length() > 20);
    }

    @Test
    void testGetUsernameFromToken() {
        // 生成令牌
        String token = jwtUtil.generateToken(testUsername, testUserId, testUserType);
        
        // 从令牌中获取用户名
        String username = jwtUtil.getUsernameFromToken(token);
        
        // 验证用户名
        assertEquals(testUsername, username);
    }

    @Test
    void testGetUserIdFromToken() {
        // 生成令牌
        String token = jwtUtil.generateToken(testUsername, testUserId, testUserType);
        
        // 从令牌中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        // 验证用户ID
        assertEquals(testUserId, userId);
    }

    @Test
    void testGetUserTypeNumberFromToken() {
        // 生成令牌
        String token = jwtUtil.generateToken(testUsername, testUserId, testUserType);
        
        // 从令牌中获取用户类型
        Integer userType = jwtUtil.getUserTypeNumberFromToken(token);
        
        // 验证用户类型
        assertEquals(testUserType, userType);
    }

    @Test
    void testGetUserTypeFromToken() {
        // 生成令牌（学生）
        String token = jwtUtil.generateToken(testUsername, testUserId, 2);
        
        // 从令牌中获取用户类型字符串
        String userType = jwtUtil.getUserTypeFromToken(token);
        
        // 验证用户类型为STUDENT
        assertEquals("STUDENT", userType);
        
        // 生成令牌（管理员）
        token = jwtUtil.generateToken(testUsername, testUserId, 1);
        
        // 从令牌中获取用户类型字符串
        userType = jwtUtil.getUserTypeFromToken(token);
        
        // 验证用户类型为ADMIN
        assertEquals("ADMIN", userType);
    }

    @Test
    void testValidateToken() {
        // 生成令牌
        String token = jwtUtil.generateToken(testUsername, testUserId, testUserType);
        
        // 验证令牌
        Boolean isValid = jwtUtil.validateToken(token, testUsername);
        
        // 验证结果为true
        assertTrue(isValid);
        
        // 验证不同用户名
        isValid = jwtUtil.validateToken(token, "wronguser");
        
        // 验证结果为false
        assertFalse(isValid);
    }

    @Test
    void testIsTokenExpired() throws Exception {
        // 设置过期时间为-1秒（已过期）
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1L);
        
        // 生成令牌
        String token = jwtUtil.generateToken(testUsername, testUserId, testUserType);
        
        // 验证令牌（已过期）
        Boolean isValid = jwtUtil.validateToken(token, testUsername);
        
        // 验证结果为false
        assertFalse(isValid);
        
        // 恢复过期时间
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600L);
    }

    @Test
    void testGetTokenFromRequest() {
        // 模拟请求头
        when(request.getHeader("Authorization")).thenReturn("Bearer test-token");
        
        // 获取令牌
        String token = jwtUtil.getTokenFromRequest(request);
        
        // 验证结果
        assertEquals("test-token", token);
        
        // 模拟无效请求头
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader test-token");
        
        // 获取令牌
        token = jwtUtil.getTokenFromRequest(request);
        
        // 验证结果为null
        assertNull(token);
    }

    @Test
    void testGetUserIdFromRequest() {
        // 生成令牌
        String token = jwtUtil.generateToken(testUsername, testUserId, testUserType);
        
        // 模拟请求头
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        
        // 从请求中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(request);
        
        // 验证用户ID
        assertEquals(testUserId, userId);
        
        // 模拟无效请求头
        when(request.getHeader("Authorization")).thenReturn(null);
        
        // 从请求中获取用户ID
        userId = jwtUtil.getUserIdFromToken(request);
        
        // 验证结果为null
        assertNull(userId);
    }
} 