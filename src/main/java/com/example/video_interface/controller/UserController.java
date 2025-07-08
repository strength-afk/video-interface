package com.example.video_interface.controller;

import com.example.video_interface.dto.LoginRequest;
import com.example.video_interface.dto.RegisterRequest;
import com.example.video_interface.model.User;
import com.example.video_interface.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 * 处理用户相关的HTTP请求，包括用户注册、登录、信息管理等操作
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    /**
     * 用户注册
     * @param registerRequest 注册请求
     * @return 注册成功的用户信息
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            log.info("收到注册请求: {}", registerRequest.getUsername());
            log.debug("注册请求详情: {}", registerRequest);
            
            User user = userService.registerUser(registerRequest);
            String token = userService.generateToken(user);
            
            log.info("用户注册成功: {}", user.getUsername());
            return ResponseEntity.ok(Map.of(
                "user", user,
                "token", token,
                "message", "注册成功"
            ));
        } catch (Exception e) {
            log.error("注册失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "error", "注册失败: " + e.getMessage(),
                "status", "error"
            ));
        }
    }

    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return JWT令牌
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("收到登录请求: {}", loginRequest.getUsername());
            User user = userService.loginUser(loginRequest);
            String token = userService.generateToken(user);
            
            log.info("用户登录成功: {}", user.getUsername());
            return ResponseEntity.ok(Map.of(
                "user", user,
                "token", token,
                "message", "登录成功"
            ));
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "error", "登录失败: " + e.getMessage(),
                "status", "error"
            ));
        }
    }

    /**
     * 用户登出
     * @param request 包含Authorization头
     * @return 成功响应
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        log.info("收到登出请求");
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                userService.logout(token);
                log.info("用户登出成功");
            }
            return ResponseEntity.ok(Map.of(
                "message", "退出登录成功",
                "status", "success"
            ));
        } catch (Exception e) {
            log.error("登出失败: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "error", "退出登录失败",
                "status", "error"
            ));
        }
    }

    /**
     * 检查用户名是否可用
     * @param username 用户名
     * @return 用户名是否可用
     */
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Object>> checkUsername(@RequestParam String username) {
        log.info("检查用户名是否可用: {}", username);
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(Map.of(
            "exists", exists,
            "available", !exists,
            "status", "success"
        ));
    }

    /**
     * 检查邮箱是否可用
     * @param email 邮箱地址
     * @return 邮箱是否可用
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
        log.info("检查邮箱是否可用: {}", email);
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(Map.of(
            "exists", exists,
            "available", !exists,
            "status", "success"
        ));
    }

    /**
     * 更新用户密码
     * @param passwordData 密码更新数据
     * @return 成功响应
     */
    @PostMapping("/update-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> passwordData) {
        try {
            log.info("收到密码更新请求");
            userService.updatePassword(
                passwordData.get("username"),
                passwordData.get("oldPassword"),
                passwordData.get("newPassword")
            );
            log.info("密码更新成功");
            return ResponseEntity.ok(Map.of(
                "message", "密码更新成功",
                "status", "success"
            ));
        } catch (Exception e) {
            log.error("更新密码失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "更新密码失败: " + e.getMessage(),
                "status", "error"
            ));
        }
    }

    /**
     * 更新用户基本信息
     * @param profileData 用户信息更新数据
     * @return 更新后的用户信息
     */
    @PostMapping("/update-profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> profileData) {
        try {
            log.info("收到用户信息更新请求");
            User updatedUser = userService.updateProfile(
                profileData.get("username"),
                profileData.get("avatar"),
                profileData.get("phoneNumber")
            );
            log.info("用户信息更新成功");
            return ResponseEntity.ok(Map.of(
                "user", updatedUser,
                "message", "用户信息更新成功",
                "status", "success"
            ));
        } catch (Exception e) {
            log.error("更新用户信息失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "更新用户信息失败: " + e.getMessage(),
                "status", "error"
            ));
        }
    }

    /**
     * 获取当前用户信息
     * @return 当前用户信息
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser() {
        log.info("收到获取当前用户信息请求");
        try {
            User user = userService.getCurrentUser();
            log.info("获取当前用户信息成功");
            return ResponseEntity.ok(Map.of(
                "user", user,
                "status", "success"
            ));
        } catch (Exception e) {
            log.error("获取当前用户信息失败: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "error", "获取用户信息失败",
                "status", "error"
            ));
        }
    }

    /**
     * 更新当前用户信息
     * @param userData 用户信息更新数据
     * @return 更新后的用户信息
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateCurrentUser(@RequestBody Map<String, String> userData) {
        try {
            log.info("收到更新当前用户信息请求");
            User updatedUser = userService.updateCurrentUser(userData);
            log.info("更新当前用户信息成功");
            return ResponseEntity.ok(Map.of(
                "user", updatedUser,
                "message", "用户信息更新成功",
                "status", "success"
            ));
        } catch (Exception e) {
            log.error("更新当前用户信息失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "更新用户信息失败: " + e.getMessage(),
                "status", "error"
            ));
        }
    }
} 