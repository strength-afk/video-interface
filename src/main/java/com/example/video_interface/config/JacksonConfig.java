package com.example.video_interface.config;

import com.example.video_interface.model.User;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson配置类
 * 配置JSON序列化和反序列化规则
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 注册Java 8时间模块
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        // 配置LocalDateTime序列化和反序列化格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        
        // 添加支持多种格式的LocalDateTime反序列化器
        javaTimeModule.addDeserializer(LocalDateTime.class, new com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer(formatter) {
            @Override
            public LocalDateTime deserialize(com.fasterxml.jackson.core.JsonParser p, com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
                String value = p.getValueAsString();
                if (value == null || value.trim().isEmpty()) {
                    return null;
                }
                
                try {
                    // 首先尝试ISO格式 (2024-01-01T00:00:00.000Z)
                    if (value.contains("T")) {
                        // 移除Z后缀，转换为本地时间
                        if (value.endsWith("Z")) {
                            value = value.substring(0, value.length() - 1);
                        }
                        return LocalDateTime.parse(value);
                    }
                    
                    // 然后尝试标准格式 (yyyy-MM-dd HH:mm:ss)
                    return LocalDateTime.parse(value, formatter);
                } catch (Exception e) {
                    throw new java.io.IOException("无法解析时间格式: " + value + ", 支持的格式: yyyy-MM-dd HH:mm:ss 或 ISO格式", e);
                }
            }
        });
        
        objectMapper.registerModule(javaTimeModule);
        
        // 禁用将日期写为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 配置反序列化特性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        
        // 注册自定义模块
        SimpleModule module = new SimpleModule();
        
        // 添加枚举反序列化器
        module.addDeserializer(User.UserStatus.class, new com.fasterxml.jackson.databind.JsonDeserializer<User.UserStatus>() {
            @Override
            public User.UserStatus deserialize(com.fasterxml.jackson.core.JsonParser p, com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
                String value = p.getValueAsString();
                if (value == null || value.trim().isEmpty()) {
                    return null;
                }
                try {
                    return User.UserStatus.valueOf(value.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        });
        
        module.addDeserializer(User.UserRole.class, new com.fasterxml.jackson.databind.JsonDeserializer<User.UserRole>() {
            @Override
            public User.UserRole deserialize(com.fasterxml.jackson.core.JsonParser p, com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
                String value = p.getValueAsString();
                if (value == null || value.trim().isEmpty()) {
                    return null;
                }
                try {
                    return User.UserRole.valueOf(value.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        });
        
        objectMapper.registerModule(module);
        
        return objectMapper;
    }
} 