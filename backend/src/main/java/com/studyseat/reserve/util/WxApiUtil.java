package com.studyseat.reserve.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSONObject;
import com.studyseat.reserve.common.ResultCode;
import com.studyseat.reserve.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 微信API工具类
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WxApiUtil {

    @Value("${wx.appid}")
    private String appid;

    @Value("${wx.secret}")
    private String secret;

    private final RestTemplate restTemplate;

    /**
     * 获取微信OpenID
     * 
     * @param code 微信临时登录凭证
     * @return OpenID
     */
    public String getOpenId(String code) {
        try {
            // 微信登录凭证校验接口URL
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={code}&grant_type=authorization_code";
            
            // 设置参数
            Map<String, String> params = new HashMap<>();
            params.put("appid", appid);
            params.put("secret", secret);
            params.put("code", code);
            
            // 发送请求
            String response = restTemplate.getForObject(url, String.class, params);
            
            // 解析响应
            JSONObject jsonObject = JSONObject.parseObject(response);
            
            // 判断是否成功
            if (jsonObject.containsKey("errcode") && jsonObject.getIntValue("errcode") != 0) {
                log.error("获取微信OpenID失败: {}", response);
                throw new BusinessException(ResultCode.WX_LOGIN_ERROR, jsonObject.getString("errmsg"));
            }
            
            // 返回OpenID
            return jsonObject.getString("openid");
        } catch (Exception e) {
            log.error("获取微信OpenID异常", e);
            throw new BusinessException(ResultCode.WX_LOGIN_ERROR, "获取OpenID失败");
        }
    }
} 