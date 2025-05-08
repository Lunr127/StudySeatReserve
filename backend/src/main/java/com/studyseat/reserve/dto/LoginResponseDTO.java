package com.studyseat.reserve.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private Long userId;
    private String username;
    private String realName;
    private Integer userType;  // 1-管理员，2-学生
    private String avatar;
} 