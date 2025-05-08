package com.studyseat.reserve.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studyseat.reserve.common.Result;
import com.studyseat.reserve.dto.LoginRequestDTO;
import com.studyseat.reserve.dto.LoginResponseDTO;
import com.studyseat.reserve.dto.WxLoginRequestDTO;
import com.studyseat.reserve.service.AuthService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Api(tags = "认证接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户名密码登录
     */
    @PostMapping("/login")
    @ApiOperation("用户名密码登录")
    public Result<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO loginResponse = authService.login(loginRequest);
        return Result.success(loginResponse);
    }

    /**
     * 微信小程序登录
     */
    @PostMapping("/wx-login")
    @ApiOperation("微信小程序登录")
    public Result<LoginResponseDTO> wxLogin(@RequestBody WxLoginRequestDTO wxLoginRequest) {
        LoginResponseDTO loginResponse = authService.wxLogin(wxLoginRequest);
        return Result.success(loginResponse);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    @ApiOperation("退出登录")
    public Result<Void> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            authService.logout(token);
        }
        return Result.success();
    }
} 