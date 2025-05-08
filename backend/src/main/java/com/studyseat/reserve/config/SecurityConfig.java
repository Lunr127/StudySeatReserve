package com.studyseat.reserve.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security配置
 * 注意：这是一个简化的安全配置，仅用于项目启动测试
 * 实际开发中需要进一步完善JWT认证等功能
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            // 允许所有静态资源和测试接口
            .antMatchers("/", "/*.html", "/favicon.ico", "/css/**", "/js/**", "/images/**").permitAll()
            // 允许H2控制台访问（仅用于测试）
            .antMatchers("/h2-console/**").permitAll()
            // Knife4j API文档
            .antMatchers("/doc.html", "/webjars/**", "/swagger-resources/**", "/v3/api-docs/**").permitAll()
            // 开发测试阶段放开所有接口
            .antMatchers("/**").permitAll()
            .anyRequest().authenticated();
        
        // 允许iframe加载H2控制台
        http.headers().frameOptions().disable();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 