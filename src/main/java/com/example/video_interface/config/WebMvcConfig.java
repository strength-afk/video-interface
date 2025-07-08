package com.example.video_interface.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import lombok.extern.slf4j.Slf4j;

/**
 * Web MVC 配置类
 * 注意：CORS配置已统一在SecurityConfig中管理，避免重复配置
 */
@Configuration
@EnableWebMvc
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {
    
    // CORS配置已统一在SecurityConfig中管理
    // 如需其他MVC配置，可在此添加
    
} 