package com.example.video_interface.controller.admin;

import com.example.video_interface.dto.admin.AdminLoginRequest;
import com.example.video_interface.model.User;
import com.example.video_interface.security.JwtTokenProvider;
import com.example.video_interface.service.admin.IAdminService;
import com.example.video_interface.util.RequestContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员控制器
 * 处理管理员相关的请求，包括登录、登出、获取个人信息等
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final IAdminService adminService;
    private final JwtTokenProvider tokenProvider;

    /**
     * 管理员登录
     * @param request 登录请求
     * @return 登录结果，包含token和用户信息
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginRequest request) {
        log.info("接收到管理员登录请求: {}", request.getUsername());
        
        try {
            // 执行登录
            User admin = adminService.adminLogin(request);
            
            // 生成token
            String token = tokenProvider.generateToken(admin);
            
            // 构造返回数据
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", admin);
            
            log.info("管理员登录成功: {}", admin.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("管理员登录失败: {} - {}", request.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("管理员登录发生错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "登录失败，请稍后重试"
            ));
        }
    }

    /**
     * 管理员登出
     * @return 登出结果
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            String token = RequestContextUtil.getAuthToken();
            if (token != null) {
                adminService.adminLogout(token);
                log.info("管理员登出成功");
            }
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            log.error("管理员登出发生错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "退出登录失败，请稍后重试"
            ));
        }
    }

    /**
     * 获取当前管理员信息
     * @return 当前管理员信息
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentAdmin() {
        try {
            User admin = adminService.getCurrentAdmin();
            log.debug("获取管理员信息成功: {}", admin.getUsername());
            return ResponseEntity.ok(admin);
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.warn("获取管理员信息失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("获取管理员信息发生错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取信息失败，请稍后重试"
            ));
        }
    }

    /**
     * 检查Token状态
     * @return Token状态信息，包含有效性和过期时间
     */
    @GetMapping("/check-status")
    public ResponseEntity<?> checkTokenStatus() {
        try {
            String token = RequestContextUtil.getAuthToken();
            if (token == null) {
                return ResponseEntity.ok(Map.of(
                    "valid", false,
                    "expiresIn", 0
                ));
            }

            // 验证token
            boolean isValid = tokenProvider.validateToken(token);
            
            // 如果token有效，计算剩余时间
            long expiresIn = 0;
            if (isValid) {
                Date expiration = tokenProvider.getExpirationDateFromToken(token);
                expiresIn = Math.max(0, (expiration.getTime() - System.currentTimeMillis()) / 1000);
            }
            
            log.debug("Token状态检查 - 有效性: {}, 剩余时间: {}秒", isValid, expiresIn);
            
            return ResponseEntity.ok(Map.of(
                "valid", isValid,
                "expiresIn", expiresIn
            ));
            
        } catch (Exception e) {
            log.error("检查Token状态时发生错误: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                "valid", false,
                "expiresIn", 0
            ));
        }
    }
} 