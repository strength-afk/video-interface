package com.example.video_interface.dto.admin;

import lombok.Data;

/**
 * 管理员登录请求DTO
 */
@Data
public class AdminLoginRequest {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
} 