package com.studyseat.reserve.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * JWT工具类
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    /**
     * 从token中获取用户名
     * 
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从token中获取过期时间
     * 
     * @param token
     * @return
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从token中获取指定的claim
     * 
     * @param <T>
     * @param token
     * @param claimsResolver
     * @return
     */
    public <T> T getClaimFromToken(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从token中获取所有的claims
     * 
     * @param token
     * @return
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * 检查token是否过期
     * 
     * @param token
     * @return
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 生成token
     * 
     * @param username
     * @return
     */
    public String generateToken(String username, Long userId, Integer userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userType", userType);
        return doGenerateToken(claims, username);
    }

    /**
     * 生成token的具体过程
     * 
     * @param claims
     * @param subject
     * @return
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 验证token
     * 
     * @param token
     * @param username
     * @return
     */
    public Boolean validateToken(String token, String username) {
        final String tokenUsername = getUsernameFromToken(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * 从token中获取用户ID
     * 
     * @param token
     * @return
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return Long.parseLong(claims.get("userId").toString());
    }

    /**
     * 从token中获取用户类型（数字）
     * 
     * @param token
     * @return 1-管理员，2-学生
     */
    public Integer getUserTypeNumberFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return Integer.parseInt(claims.get("userType").toString());
    }
    
    /**
     * 从token中获取用户类型（字符串形式，适用于Security角色）
     * 
     * @param token
     * @return "ADMIN" 或 "STUDENT"
     */
    public String getUserTypeFromToken(String token) {
        Integer userType = getUserTypeNumberFromToken(token);
        return userType == 1 ? "ADMIN" : "STUDENT";
    }
    
    /**
     * 从请求中获取token
     * 
     * @param request
     * @return
     */
    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(tokenHeader);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(tokenHead + " ")) {
            return bearerToken.substring(tokenHead.length() + 1);
        }
        return null;
    }
    
    /**
     * 从请求中获取用户ID
     * 
     * @param request
     * @return
     */
    public Long getUserIdFromToken(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token != null) {
            return getUserIdFromToken(token);
        }
        return null;
    }
    
    /**
     * 从请求中获取用户类型
     * 
     * @param request
     * @return
     */
    public String getUserTypeFromToken(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token != null) {
            return getUserTypeFromToken(token);
        }
        return null;
    }
} 