package com.example.video_interface.service.common;

/**
 * 邮箱验证码服务接口
 * 提供发送邮箱验证码等通用功能
 */
public interface IEmailCodeService {
    /**
     * 发送邮箱验证码
     * @param email 邮箱地址
     */
    void sendEmailCode(String email);
} 