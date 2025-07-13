package com.example.video_interface.controller.h5;

import com.example.video_interface.service.common.ICaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * H5验证码控制器
 * 提供验证码生成、刷新和验证接口
 */
@Slf4j
@RestController
@RequestMapping("/users/captcha")
@RequiredArgsConstructor
public class H5CaptchaController {
    private final ICaptchaService captchaService;

    /**
     * 获取验证码
     * @param sessionId 可选的会话ID
     * @return 验证码图片和会话ID
     */
    @GetMapping
    public ResponseEntity<?> getCaptcha(@RequestParam(required = false) String sessionId) {
        log.debug("获取验证码请求 - 会话ID: {}", sessionId);
        
        try {
            Map<String, String> result = captchaService.generateCaptcha(sessionId);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "captcha", result.get("captcha"),
                "sessionId", result.get("sessionId")
            ));
        } catch (Exception e) {
            log.error("生成验证码失败: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                "status", "error",
                "error", "获取验证码失败，请重试"
            ));
        }
    }

    /**
     * 刷新验证码
     * @param request 包含会话ID的请求
     * @return 新的验证码图片
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshCaptcha(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        log.debug("刷新验证码请求 - 会话ID: {}", sessionId);
        
        if (sessionId == null) {
            return ResponseEntity.ok(Map.of(
                "status", "error",
                "error", "无效的会话ID"
            ));
        }
        
        try {
            Map<String, String> result = captchaService.refreshCaptcha(sessionId);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "captcha", result.get("captcha")
            ));
        } catch (Exception e) {
            log.error("刷新验证码失败: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                "status", "error",
                "error", "刷新验证码失败，请重试"
            ));
        }
    }

    /**
     * 验证验证码
     * @param request 包含会话ID和验证码的请求
     * @return 验证结果
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCaptcha(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        String captcha = request.get("captcha");
        log.debug("验证验证码请求 - 会话ID: {}", sessionId);
        
        if (sessionId == null || captcha == null) {
            return ResponseEntity.ok(Map.of(
                "status", "error",
                "error", "无效的验证码或会话ID"
            ));
        }
        
        try {
            boolean isValid = captchaService.verifyCaptcha(sessionId, captcha);
            return ResponseEntity.ok(Map.of(
                "status", isValid ? "success" : "error",
                "message", isValid ? "验证通过" : "验证码错误或已过期"
            ));
        } catch (Exception e) {
            log.error("验证码验证失败: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                "status", "error",
                "error", "验证失败，请重试"
            ));
        }
    }
} 