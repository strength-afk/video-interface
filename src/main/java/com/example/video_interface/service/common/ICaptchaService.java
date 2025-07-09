package com.example.video_interface.service.common;

import java.util.Map;

/**
 * 验证码服务接口
 * 提供验证码生成、验证和刷新功能
 */
public interface ICaptchaService {
    /**
     * 生成验证码
     * @param sessionId 可选的会话ID，如果为null则创建新会话
     * @return 包含验证码图片Base64和会话ID的Map
     */
    Map<String, String> generateCaptcha(String sessionId);

    /**
     * 刷新验证码
     * @param sessionId 会话ID
     * @return 包含新验证码图片Base64和会话ID的Map
     */
    Map<String, String> refreshCaptcha(String sessionId);

    /**
     * 验证验证码
     * @param sessionId 会话ID
     * @param captcha 用户输入的验证码
     * @return 验证是否通过
     */
    boolean verifyCaptcha(String sessionId, String captcha);
} 