package com.example.video_interface.controller;

import com.example.video_interface.dto.LoginRequest;
import com.example.video_interface.dto.RegisterRequest;
import com.example.video_interface.model.User;
import com.example.video_interface.service.UserService;
import com.example.video_interface.service.CaptchaService;
import com.example.video_interface.service.H5LoginFailureService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

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
    private final CaptchaService captchaService;
    private final H5LoginFailureService h5LoginFailureService;

    /**
     * 获取客户端真实IP地址
     * @param request HTTP请求
     * @return 客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

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
                "error", e.getMessage(),  // 直接返回错误信息，不加前缀
                "status", "error"
            ));
        }
    }

    /**
     * 统一认证端点 - 处理登录和注册
     * 根据用户是否存在自动判断是登录还是注册
     * @param authRequest 认证请求（包含用户名和密码）
     * @return JWT令牌和用户信息
     */
    @PostMapping("/auth")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> authRequest, HttpServletRequest request) {
        try {
            String username = authRequest.get("username");
            String password = authRequest.get("password");
            String captcha = authRequest.get("captcha");
            String sessionId = authRequest.get("sessionId");
            
            log.info("收到H5统一认证请求: {}", username);
            
            if (username == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "用户名和密码不能为空",
                    "status", "error"
                ));
            }
            
            // 检查用户是否已存在
            boolean userExists = userService.existsByUsername(username);
            
            if (userExists) {
                // 用户存在，执行H5登录（无锁定机制）
                log.info("用户已存在，执行H5登录: {}", username);
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setUsername(username);
                loginRequest.setPassword(password);
                loginRequest.setCaptcha(captcha);
                loginRequest.setSessionId(sessionId);
                
                User user = userService.h5LoginUser(loginRequest);  // 使用专门的H5登录方法
                String token = userService.generateToken(user);
                
                // 登录成功，清除失败次数
                String clientIp = getClientIp(request);
                h5LoginFailureService.clearFailCount(clientIp);
                
                log.info("H5用户登录成功: {}", user.getUsername());
                return ResponseEntity.ok(Map.of(
                    "user", user,
                    "token", token,
                    "message", "登录成功",
                    "action", "login"
                ));
            } else {
                // 用户不存在，执行注册
                log.info("用户不存在，执行注册: {}", username);
                RegisterRequest registerRequest = new RegisterRequest();
                registerRequest.setUsername(username);
                registerRequest.setPassword(password);
                
                User user = userService.registerUser(registerRequest);
                String token = userService.generateToken(user);
                
                log.info("用户注册成功: {}", user.getUsername());
                return ResponseEntity.ok(Map.of(
                    "user", user,
                    "token", token,
                    "message", "注册成功",
                    "action", "register"
                ));
            }
        } catch (Exception e) {
            log.error("H5认证失败: {}", e.getMessage(), e);
            
            // 获取客户端IP
            String clientIp = getClientIp(request);
            
            // 检查是否是登录失败（排除注册相关错误）
            String errorMessage = e.getMessage();
            boolean isLoginFailure = errorMessage != null && 
                (errorMessage.contains("用户名或密码错误") || 
                 errorMessage.contains("密码错误") || 
                 errorMessage.contains("用户不存在") ||
                 errorMessage.contains("验证码")) &&
                !errorMessage.contains("注册频繁") &&  // 排除注册限制错误
                !errorMessage.contains("用户名已被使用") &&  // 排除注册验证错误
                !errorMessage.contains("用户名长度必须") &&  // 排除注册验证错误
                !errorMessage.contains("密码长度必须") &&   // 排除注册验证错误
                !errorMessage.contains("用户名只能包含");   // 排除注册验证错误
            
            boolean needCaptcha = false;
            if (isLoginFailure) {
                // 增加失败次数
                int failCount = h5LoginFailureService.incrementFailCount(clientIp);
                needCaptcha = h5LoginFailureService.needCaptcha(failCount);
                log.info("H5登录失败，IP: {}, 失败次数: {}, 需要验证码: {}", clientIp, failCount, needCaptcha);
            }
            
            return ResponseEntity.badRequest().body(Map.of(
                "error", errorMessage,  // 直接返回错误信息，不加前缀
                "status", "error",
                "needCaptcha", needCaptcha
            ));
        }
    }

    /**
     * 用户登录 (支持H5验证码和失败计数)
     * @param loginRequest 登录请求
     * @param request HTTP请求
     * @return JWT令牌
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            log.info("收到H5登录请求: {}", loginRequest.getUsername());
            
            // 使用H5专用登录方法（支持验证码和失败计数，但不会自动注册）
            User user = userService.h5LoginUser(loginRequest);
            String token = userService.generateToken(user);
            
            // 登录成功，清除失败次数
            String clientIp = getClientIp(request);
            h5LoginFailureService.clearFailCount(clientIp);
            
            log.info("H5用户登录成功: {}", user.getUsername());
            return ResponseEntity.ok(Map.of(
                "user", user,
                "token", token,
                "message", "登录成功"
            ));
        } catch (Exception e) {
            log.error("H5登录失败: {}", e.getMessage(), e);
            
            // 获取客户端IP
            String clientIp = getClientIp(request);
            
            // 检查是否是登录失败（需要计入失败次数）
            String errorMessage = e.getMessage();
            boolean isLoginFailure = errorMessage != null && 
                (errorMessage.contains("用户名或密码错误") || 
                 errorMessage.contains("密码错误") || 
                 errorMessage.contains("用户不存在") ||
                 errorMessage.contains("验证码"));
            
            boolean needCaptcha = false;
            if (isLoginFailure) {
                // 增加失败次数
                int failCount = h5LoginFailureService.incrementFailCount(clientIp);
                needCaptcha = h5LoginFailureService.needCaptcha(failCount);
                log.info("H5登录失败，IP: {}, 失败次数: {}, 需要验证码: {}", clientIp, failCount, needCaptcha);
            }
            
            return ResponseEntity.badRequest().body(Map.of(
                "error", errorMessage,  // 直接返回错误信息，不加前缀
                "status", "error",
                "needCaptcha", needCaptcha
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

    /**
     * 管理员登录
     * @param loginRequest 登录请求
     * @return JWT令牌和管理员信息
     */
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("收到管理员登录请求: {}", loginRequest.getUsername());
            User admin = userService.adminLogin(loginRequest);
            String token = userService.generateToken(admin);
            
            log.info("管理员登录成功: {}", admin.getUsername());
            return ResponseEntity.ok(Map.of(
                "admin", admin,
                "token", token,
                "message", "管理员登录成功",
                "role", admin.getRole().name()
            ));
        } catch (Exception e) {
            log.error("管理员登录失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "error", "管理员登录失败: " + e.getMessage(),
                "status", "error"
            ));
        }
    }

    /**
     * 创建初始管理员账号
     * @param adminData 管理员账号数据
     * @return 创建成功的管理员信息
     */
    @PostMapping("/admin/init")
    public ResponseEntity<?> createInitialAdmin(@RequestBody Map<String, String> adminData) {
        try {
            log.info("收到创建初始管理员请求");
            
            // 检查是否已存在管理员
            if (userService.hasAdminUser()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "系统已存在管理员账号，无法重复创建",
                    "status", "error"
                ));
            }
            
            User admin = userService.createInitialAdmin(
                adminData.get("username"),
                adminData.get("password"),
                adminData.get("email")
            );
            
            log.info("初始管理员创建成功: {}", admin.getUsername());
            return ResponseEntity.ok(Map.of(
                "admin", admin,
                "message", "初始管理员创建成功",
                "status", "success"
            ));
        } catch (Exception e) {
            log.error("创建初始管理员失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "error", "创建初始管理员失败: " + e.getMessage(),
                "status", "error"
            ));
        }
    }

    /**
     * 检查管理员状态
     * @return 管理员状态信息
     */
    @GetMapping("/admin/check-status")
    public ResponseEntity<?> checkAdminStatus() {
        try {
            boolean hasAdmin = userService.hasAdminUser();
            long adminCount = userService.getAdminCount();
            
            return ResponseEntity.ok(Map.of(
                "hasAdmin", hasAdmin,
                "adminCount", adminCount,
                "needsInitialization", !hasAdmin,
                "status", "success"
            ));
        } catch (Exception e) {
            log.error("检查管理员状态失败: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "error", "检查管理员状态失败",
                "status", "error"
            ));
        }
    }

    /**
     * 获取验证码
     * @param sessionId 会话ID（可选，如果不提供则自动生成）
     * @return 验证码图片的Base64编码和会话ID
     */
    @GetMapping("/captcha")
    public ResponseEntity<?> getCaptcha(@RequestParam(required = false) String sessionId) {
        try {
            // 如果没有提供sessionId，则生成一个
            if (sessionId == null || sessionId.trim().isEmpty()) {
                sessionId = UUID.randomUUID().toString();
            }
            
            log.debug("收到获取验证码请求: sessionId={}", sessionId);
            String captchaImage = captchaService.generateCaptcha(sessionId);
            
            return ResponseEntity.ok(Map.of(
                "captcha", captchaImage,
                "sessionId", sessionId,
                "status", "success",
                "message", "验证码生成成功"
            ));
        } catch (Exception e) {
            log.error("获取验证码失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "获取验证码失败",
                "status", "error"
            ));
        }
    }

    /**
     * 刷新验证码
     * @param sessionId 会话ID
     * @return 新的验证码图片Base64编码
     */
    @PostMapping("/captcha/refresh")
    public ResponseEntity<?> refreshCaptcha(@RequestBody Map<String, String> requestData) {
        try {
            String sessionId = requestData.get("sessionId");
            if (sessionId == null || sessionId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "会话ID不能为空",
                    "status", "error"
                ));
            }
            
            log.debug("收到刷新验证码请求: sessionId={}", sessionId);
            
            // 删除旧验证码
            captchaService.refreshCaptcha(sessionId);
            
            // 生成新验证码
            String captchaImage = captchaService.generateCaptcha(sessionId);
            
            return ResponseEntity.ok(Map.of(
                "captcha", captchaImage,
                "sessionId", sessionId,
                "status", "success",
                "message", "验证码刷新成功"
            ));
        } catch (Exception e) {
            log.error("刷新验证码失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "刷新验证码失败",
                "status", "error"
            ));
        }
    }

    /**
     * 验证验证码
     * @param verifyData 验证数据（包含sessionId和验证码）
     * @return 验证结果
     */
    @PostMapping("/captcha/verify")
    public ResponseEntity<?> verifyCaptcha(@RequestBody Map<String, String> verifyData) {
        try {
            String sessionId = verifyData.get("sessionId");
            String captcha = verifyData.get("captcha");
            
            if (sessionId == null || captcha == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "会话ID和验证码不能为空",
                    "status", "error"
                ));
            }
            
            log.debug("收到验证码验证请求: sessionId={}", sessionId);
            boolean isValid = captchaService.verifyCaptcha(sessionId, captcha);
            
            if (isValid) {
                return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "status", "success",
                    "message", "验证码验证成功"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "status", "error",
                    "error", "验证码错误或已过期"
                ));
            }
        } catch (Exception e) {
            log.error("验证码验证失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "valid", false,
                "error", "验证码验证失败",
                "status", "error"
            ));
        }
    }


} 