package com.studyseat.reserve.dto;

import lombok.Data;

@Data
public class WxLoginRequestDTO {
    /**
     * 微信登录code
     */
    private String code;

    /**
     * 用户信息
     */
    private WxUserInfo userInfo;

    @Data
    public static class WxUserInfo {
        private String nickName;
        private String avatarUrl;
        private Byte gender;
        private String city;
        private String province;
        private String country;
        private String language;
    }
} 