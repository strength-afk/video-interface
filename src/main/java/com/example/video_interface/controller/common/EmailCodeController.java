package com.example.video_interface.controller.common;

import com.example.video_interface.service.common.IEmailCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 邮箱验证码通用控制器
 */
@RestController
@RequestMapping("/common/email-code")
@RequiredArgsConstructor
public class EmailCodeController {
    private final IEmailCodeService emailCodeService;

    /**
     * 发送邮箱验证码
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendEmailCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        try {
            emailCodeService.sendEmailCode(email);
            return ResponseEntity.ok(Map.of("success", true, "message", "验证码已发送"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "发送失败，请稍后重试"));
        }
    }
} 