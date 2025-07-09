package com.example.video_interface.dto.h5;

import lombok.Data;

/**
 * H5登录请求DTO
 */
@Data
public class H5LoginRequest {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 验证码（可选）
     */
    private String captcha;

    /**
     * 验证码会话ID（可选）
     */
    private String sessionId;
} 