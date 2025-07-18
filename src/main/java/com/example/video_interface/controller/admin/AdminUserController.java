package com.example.video_interface.controller.admin;

import com.example.video_interface.dto.admin.AdminUserDTO;
import com.example.video_interface.dto.admin.AdminUserRequest;
import com.example.video_interface.dto.admin.UserStatistics;
import com.example.video_interface.model.User;
import com.example.video_interface.repository.UserRepository;
import com.example.video_interface.service.admin.IAdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理后台用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final IAdminUserService adminUserService;
    private final UserRepository userRepository;

    /**
     * 分页查询用户列表
     * @param request 查询请求
     * @return 分页用户列表
     */
    @PostMapping("/list")
    public ResponseEntity<?> getUserList(@RequestBody AdminUserRequest request) {
        try {
            Page<AdminUserDTO> userPage = adminUserService.getUserList(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", userPage.getContent());
            response.put("totalElements", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());
            response.put("currentPage", userPage.getNumber() + 1);
            response.put("size", userPage.getSize());
            
            log.debug("获取用户列表成功，共{}个用户", userPage.getTotalElements());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", response
            ));
        } catch (Exception e) {
            log.error("获取用户列表失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取用户列表失败，请稍后重试"
            ));
        }
    }

    /**
     * 获取用户详情
     * @param requestBody 包含id的请求体
     * @return 用户详情
     */
    @PostMapping("/detail")
    public ResponseEntity<?> getUserById(@RequestBody Map<String, Object> requestBody) {
        try {
            Long userId = Long.valueOf(requestBody.get("id").toString());
            AdminUserDTO user = adminUserService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "用户不存在"
                ));
            }
            log.debug("获取用户详情成功: {}", user.getUsername());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", user
            ));
        } catch (Exception e) {
            log.error("获取用户详情失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取用户详情失败，请稍后重试"
            ));
        }
    }

    /**
     * 更新用户信息
     * @param request 用户更新请求
     * @return 更新后的用户信息
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody AdminUserRequest request) {
        try {
            if (request.getId() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "用户ID不能为空"
                ));
            }
            
            AdminUserDTO user = adminUserService.updateUser(request);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "用户不存在"
                ));
            }
            
            log.debug("更新用户成功: {}", user.getUsername());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", user,
                "message", "更新成功"
            ));
        } catch (Exception e) {
            log.error("更新用户失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "更新用户失败，请稍后重试"
            ));
        }
    }

    /**
     * 删除用户
     * @param requestBody 包含id的请求体
     * @return 操作结果
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestBody Map<String, Object> requestBody) {
        try {
            Long userId = Long.valueOf(requestBody.get("id").toString());
            adminUserService.deleteUser(userId);
            log.debug("删除用户成功: {}", userId);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "删除成功"
            ));
        } catch (Exception e) {
            log.error("删除用户失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "删除用户失败，请稍后重试"
            ));
        }
    }

    /**
     * 锁定用户账户
     * @param requestBody 包含id、reason和lockDuration的请求体
     * @return 锁定后的用户信息
     */
    @PostMapping("/lock")
    public ResponseEntity<?> lockUser(@RequestBody Map<String, Object> requestBody) {
        try {
            Long userId = Long.valueOf(requestBody.get("id").toString());
            String reason = requestBody.get("reason").toString();
            Integer lockDuration = null;
            if (requestBody.containsKey("lockDuration") && requestBody.get("lockDuration") != null) {
                lockDuration = Integer.valueOf(requestBody.get("lockDuration").toString());
            }
            
            AdminUserDTO user = adminUserService.lockUser(userId, reason, lockDuration);
            log.debug("锁定用户成功: {}", user.getUsername());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", user,
                "message", "锁定成功"
            ));
        } catch (Exception e) {
            log.error("锁定用户失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "锁定用户失败，请稍后重试"
            ));
        }
    }

    /**
     * 解锁用户账户
     * @param requestBody 包含id的请求体
     * @return 解锁后的用户信息
     */
    @PostMapping("/unlock")
    public ResponseEntity<?> unlockUser(@RequestBody Map<String, Object> requestBody) {
        try {
            Long userId = Long.valueOf(requestBody.get("id").toString());
            AdminUserDTO user = adminUserService.unlockUser(userId);
            log.debug("解锁用户成功: {}", user.getUsername());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", user,
                "message", "解锁成功"
            ));
        } catch (Exception e) {
            log.error("解锁用户失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "解锁用户失败，请稍后重试"
            ));
        }
    }

    /**
     * 重置用户登录失败次数
     * @param requestBody 包含id的请求体
     * @return 重置后的用户信息
     */
    @PostMapping("/reset-login-attempts")
    public ResponseEntity<?> resetLoginAttempts(@RequestBody Map<String, Object> requestBody) {
        try {
            Long userId = Long.valueOf(requestBody.get("id").toString());
            AdminUserDTO user = adminUserService.resetLoginAttempts(userId);
            log.debug("重置用户登录失败次数成功: {}", user.getUsername());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", user,
                "message", "重置成功"
            ));
        } catch (Exception e) {
            log.error("重置用户登录失败次数失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "重置登录失败次数失败，请稍后重试"
            ));
        }
    }

    /**
     * 设置用户VIP状态
     * @param requestBody 包含id、isVip和expireTime的请求体
     * @return 设置后的用户信息
     */
    @PostMapping("/vip")
    public ResponseEntity<?> setVipStatus(@RequestBody Map<String, Object> requestBody) {
        try {
            Long userId = Long.valueOf(requestBody.get("id").toString());
            Boolean isVip = Boolean.valueOf(requestBody.get("isVip").toString());
            LocalDateTime expireTime = null;
            
            if (requestBody.containsKey("expireTime") && requestBody.get("expireTime") != null) {
                String expireTimeStr = requestBody.get("expireTime").toString();
                try {
                    // 处理ISO格式的时间字符串，移除时区信息
                    if (expireTimeStr.contains("T") && expireTimeStr.contains("Z")) {
                        // 移除Z后缀，转换为本地时间
                        expireTimeStr = expireTimeStr.replace("Z", "");
                    }
                    // 尝试解析ISO格式的时间字符串
                    expireTime = LocalDateTime.parse(expireTimeStr);
                } catch (Exception e) {
                    log.warn("时间格式解析失败: {}, 错误: {}", expireTimeStr, e.getMessage());
                    return ResponseEntity.badRequest().body(Map.of(
                        "code", 400,
                        "message", "时间格式不正确，请使用正确的日期时间格式"
                    ));
                }
            }
            
            log.debug("设置VIP状态参数: userId={}, isVip={}, expireTime={}", userId, isVip, expireTime);
            AdminUserDTO user = adminUserService.setVipStatus(userId, isVip, expireTime);
            log.debug("设置用户VIP状态成功: {}", user.getUsername());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", user,
                "message", "设置成功"
            ));
        } catch (Exception e) {
            log.error("设置用户VIP状态失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "设置VIP状态失败，请稍后重试"
            ));
        }
    }

    /**
     * 调整用户账户余额
     * @param requestBody 包含id、amount和reason的请求体
     * @return 调整后的用户信息
     */
    @PostMapping("/balance")
    public ResponseEntity<?> adjustBalance(@RequestBody Map<String, Object> requestBody) {
        try {
            Long userId = Long.valueOf(requestBody.get("id").toString());
            BigDecimal amount = new BigDecimal(requestBody.get("amount").toString());
            String reason = requestBody.get("reason").toString();
            
            AdminUserDTO user = adminUserService.adjustBalance(userId, amount, reason);
            log.debug("调整用户余额成功: {}, 新余额: {}", user.getUsername(), user.getAccountBalance());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", user,
                "message", "调整成功"
            ));
        } catch (Exception e) {
            log.error("调整用户余额失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "调整余额失败，请稍后重试"
            ));
        }
    }

    /**
     * 获取用户统计信息
     * @return 用户统计信息
     */
    @PostMapping("/statistics")
    public ResponseEntity<?> getUserStatistics() {
        try {
            UserStatistics statistics = adminUserService.getUserStatistics();
            log.debug("获取用户统计信息成功");
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", statistics
            ));
        } catch (Exception e) {
            log.error("获取用户统计信息失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取统计信息失败，请稍后重试"
            ));
        }
    }
    
    /**
     * 查看当前锁定用户状态
     * @return 锁定用户信息
     */
    @PostMapping("/locked-users")
    public ResponseEntity<?> getLockedUsers() {
        try {
            // 查找所有已锁定的用户
            List<User> lockedUsers = userRepository.findByIsLockedTrue();
            
            List<Map<String, Object>> userInfo = lockedUsers.stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("username", user.getUsername());
                    userMap.put("isLocked", user.getIsLocked());
                    userMap.put("lockReason", user.getLockReason());
                    userMap.put("lockTime", user.getLockTime());
                    userMap.put("unlockTime", user.getUnlockTime());
                    userMap.put("currentTime", LocalDateTime.now());
                    return userMap;
                })
                .collect(java.util.stream.Collectors.toList());
            
            log.info("当前锁定用户数量: {}", lockedUsers.size());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", userInfo
            ));
        } catch (Exception e) {
            log.error("获取锁定用户信息失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取锁定用户信息失败，请稍后重试"
            ));
        }
    }
} 