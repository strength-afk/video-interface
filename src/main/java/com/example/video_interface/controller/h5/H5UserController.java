package com.example.video_interface.controller.h5;

import com.example.video_interface.dto.h5.H5LoginRequest;
import com.example.video_interface.dto.h5.H5RegisterRequest;
import com.example.video_interface.model.User;
import com.example.video_interface.security.JwtTokenProvider;
import com.example.video_interface.service.h5.IH5UserService;
import com.example.video_interface.util.RequestContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * H5用户控制器
 * 处理H5端用户相关的请求，包括注册、登录、登出、获取个人信息等
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class H5UserController {
    private final IH5UserService h5UserService;
    private final JwtTokenProvider tokenProvider;

    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody H5RegisterRequest request) {
        log.info("📝 接收到用户注册请求: {}", request.getUsername());
        
        try {
            // 执行注册
            User user = h5UserService.registerUser(request);
            
            // 生成token
            String token = tokenProvider.generateToken(user);
            
            // 构造返回数据
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);
            
            log.info(" 用户注册成功: {}", user.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn(" 用户注册失败: {} - {}", request.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error(" 用户注册发生错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "注册失败，请稍后重试"
            ));
        }
    }

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录结果，包含token和用户信息
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody H5LoginRequest request) {
        log.info("接收到用户登录请求: {}", request.getUsername());
        
        try {
            // 执行登录
            Map<String, Object> loginResult = h5UserService.h5Login(request);
            User user = (User) loginResult.get("user");
            boolean needCaptcha = (boolean) loginResult.get("needCaptcha");
            
            // 生成token
            String token = tokenProvider.generateToken(user);
            
            // 构造返回数据
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);
            response.put("needCaptcha", needCaptcha);
            
            log.info("用户登录成功: {}", user.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("用户登录失败: {} - {}", request.getUsername(), e.getMessage());
            
            // 解析错误信息中的needCaptcha状态
            String errorMessage = e.getMessage();
            boolean needCaptcha = false;
            
            if (errorMessage.contains("needCaptcha")) {
                try {
                    // 移除大括号并分割字符串
                    String[] parts = errorMessage.substring(1, errorMessage.length() - 1).split(", ");
                    Map<String, String> errorMap = new HashMap<>();
                    for (String part : parts) {
                        String[] keyValue = part.split("=");
                        errorMap.put(keyValue[0], keyValue[1]);
                    }
                    
                    errorMessage = errorMap.get("message");
                    needCaptcha = Boolean.parseBoolean(errorMap.get("needCaptcha"));
                } catch (Exception ex) {
                    log.error("解析错误信息失败: {}", ex.getMessage());
                }
            }
            
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", errorMessage,
                "needCaptcha", needCaptcha
            ));
        } catch (Exception e) {
            log.error("用户登录发生错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "登录失败，请稍后重试",
                "needCaptcha", false
            ));
        }
    }

    /**
     * 用户登出
     * @return 登出结果
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            String token = RequestContextUtil.getAuthToken();
            if (token != null) {
                h5UserService.logout(token);
                log.info(" 用户登出成功");
            }
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            log.error(" 用户登出发生错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "退出登录失败，请稍后重试"
            ));
        }
    }

    /**
     * 获取当前用户信息
     * @return 当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            User user = h5UserService.getCurrentUser();
            log.debug(" 获取用户信息成功: {}", user.getUsername());
            return ResponseEntity.ok(user);
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.warn(" 获取用户信息失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error(" 获取用户信息发生错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取信息失败，请稍后重试"
            ));
        }
    }

    /**
     * 更新当前用户信息
     * @param userData 用户信息更新数据
     * @return 更新后的用户信息
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(@RequestBody Map<String, String> userData) {
        try {
            User updatedUser = h5UserService.updateCurrentUser(userData);
            log.info(" 用户信息更新成功: {}", updatedUser.getUsername());
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            log.warn(" 用户信息更新失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error(" 用户信息更新发生错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "更新信息失败，请稍后重试"
            ));
        }
    }
} 