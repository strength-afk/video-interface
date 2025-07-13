package com.example.video_interface.controller.admin;

import com.example.video_interface.dto.admin.AdminLoginRequest;
import com.example.video_interface.dto.admin.AdminManagementRequest;
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
 * 处理管理员相关的请求，包括登录、登出、获取个人信息、管理员管理等
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

    /**
     * 获取管理员列表
     * @param params 查询参数
     * @return 管理员列表
     */
    @PostMapping("/list")
    public ResponseEntity<?> getAdminList(@RequestBody Map<String, Object> params) {
        log.info("获取管理员列表请求，参数: {}", params);
        
        try {
            // 安全地获取参数值，避免类型转换异常
            Integer page = null;
            Integer size = null;
            String username = null;
            String email = null;
            Boolean enabled = null;
            
            if (params.get("page") != null) {
                if (params.get("page") instanceof Integer) {
                    page = (Integer) params.get("page");
                } else {
                    page = Integer.valueOf(params.get("page").toString());
                }
            }
            
            if (params.get("size") != null) {
                if (params.get("size") instanceof Integer) {
                    size = (Integer) params.get("size");
                } else {
                    size = Integer.valueOf(params.get("size").toString());
                }
            }
            
            if (params.get("username") != null) {
                username = params.get("username").toString();
            }
            
            if (params.get("email") != null) {
                email = params.get("email").toString();
            }
            
            if (params.get("enabled") != null) {
                if (params.get("enabled") instanceof Boolean) {
                    enabled = (Boolean) params.get("enabled");
                } else {
                    enabled = Boolean.valueOf(params.get("enabled").toString());
                }
            }
            
            AdminManagementRequest request = AdminManagementRequest.builder()
                .page(page)
                .size(size)
                .username(username)
                .email(email)
                .enabled(enabled)
                .build();
                
            log.info("构建的请求对象: {}", request);
            Map<String, Object> result = adminService.getAdminList(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取管理员列表失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取管理员列表失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 创建管理员
     * @param params 创建参数
     * @return 创建结果
     */
    @PostMapping("/create")
    public ResponseEntity<?> createAdmin(@RequestBody Map<String, Object> params) {
        try {
            String username = (String) params.get("username");
            log.info("创建管理员: {}", username);
            
            AdminManagementRequest request = AdminManagementRequest.builder()
                .username((String) params.get("username"))
                .password((String) params.get("password"))
                .email((String) params.get("email"))
                .enabled(true)
                .build();
                
            Map<String, Object> result = adminService.createAdmin(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("创建管理员失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 更新管理员信息
     * @param params 更新参数
     * @return 更新结果
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateAdmin(@RequestBody Map<String, Object> params) {
        try {
            Long adminId = Long.valueOf(params.get("id").toString());
            log.info("更新管理员信息: {}", adminId);
            
            AdminManagementRequest request = AdminManagementRequest.builder()
                .id(adminId)
                .username((String) params.get("username"))
                .password((String) params.get("password"))
                .email((String) params.get("email"))
                .build();
                
            Map<String, Object> result = adminService.updateAdmin(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("更新管理员信息失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 删除管理员
     * @param params 删除参数
     * @return 删除结果
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteAdmin(@RequestBody Map<String, Object> params) {
        try {
            Long adminId = Long.valueOf(params.get("id").toString());
            log.info("删除管理员: {}", adminId);
            
            AdminManagementRequest request = AdminManagementRequest.builder()
                .id(adminId)
                .build();
                
            Map<String, Object> result = adminService.deleteAdmin(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("删除管理员失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 启用/禁用管理员
     * @param params 状态参数
     * @return 操作结果
     */
    @PostMapping("/toggle-status")
    public ResponseEntity<?> toggleAdminStatus(@RequestBody Map<String, Object> params) {
        try {
            Long adminId = Long.valueOf(params.get("id").toString());
            Boolean enabled = (Boolean) params.get("enabled");
            log.info("切换管理员状态: {}, 启用状态: {}", adminId, enabled);
            
            AdminManagementRequest request = AdminManagementRequest.builder()
                .id(adminId)
                .enabled(enabled)
                .build();
                
            Map<String, Object> result = adminService.toggleAdminStatus(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("切换管理员状态失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
} 