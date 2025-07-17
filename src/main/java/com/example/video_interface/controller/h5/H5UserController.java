package com.example.video_interface.controller.h5;

import com.example.video_interface.dto.h5.H5LoginRequest;
import com.example.video_interface.dto.h5.H5RegisterRequest;
import com.example.video_interface.model.User;

import com.example.video_interface.security.JwtTokenProvider;
import com.example.video_interface.service.h5.IH5UserService;
import com.example.video_interface.util.RequestContextUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;
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
    private final RedisTemplate<String, String> stringRedisTemplate;

    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody H5RegisterRequest request) {
        log.info(" 接收到用户注册请求: {}", request.getUsername());
        
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
            boolean kickedOtherDevice = (boolean) loginResult.get("kickedOtherDevice");
            
            // 生成token
            String token = tokenProvider.generateToken(user);
            
            // 记录设备登录信息
            recordUserLoginDevice(user.getUsername(), token);
            
            // 构造返回数据
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);
            response.put("needCaptcha", needCaptcha);
            response.put("kickedOtherDevice", kickedOtherDevice);
            
            log.info("用户登录成功: {} (顶掉其他设备: {})", user.getUsername(), kickedOtherDevice);
            return ResponseEntity.ok(response);
            

        } catch (IllegalArgumentException e) {
            log.warn("用户登录失败: {} - {}", request.getUsername(), e.getMessage());
            
            // 检查是否需要验证码
            boolean needCaptcha = false;
            if (e.getMessage().contains("请输入验证码")) {
                needCaptcha = true;
            }
            
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage(),
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
                
                // 清除设备登录记录
                clearUserLoginDevice(token);
                
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

    /**
     * 修改当前用户密码
     * @param body { oldPassword: 原密码, newPassword: 新密码 }
     * @return 修改结果
     */
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body) {
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "参数不能为空"
            ));
        }
        try {
            h5UserService.changePassword(oldPassword, newPassword);
            log.info("用户修改密码成功");
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "密码修改成功"
            ));
        } catch (IllegalArgumentException e) {
            log.warn("用户修改密码失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("用户修改密码发生错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "密码修改失败，请稍后重试"
            ));
        }
    }

    /**
     * H5端会员激活码激活接口
     * @param body { activationCode: "xxxx" }
     * @return 激活结果
     */
    @PostMapping("/activate-vip-code")
    public ResponseEntity<?> activateVipCode(@RequestBody Map<String, String> body) {
        String activationCode = body.get("activationCode");
        if (activationCode == null || activationCode.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "激活码不能为空"));
        }
        try {
            User currentUser = h5UserService.getCurrentUser();
            Map<String, Object> result = h5UserService.activateVipCode(activationCode, currentUser.getId());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "激活失败，请稍后重试"));
        }
    }

    /**
     * H5端充值激活码充值接口
     * @param body { code: "xxxx" }
     * @return 充值结果
     */
    @PostMapping("/recharge")
    public ResponseEntity<?> rechargeByCode(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "充值码不能为空"));
        }
        try {
            User currentUser = h5UserService.getCurrentUser();
            Map<String, Object> result = h5UserService.rechargeByCode(code, currentUser.getId());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "充值失败，请稍后重试"));
        }
    }

    /**
     * 绑定邮箱
     */
    @PostMapping("/bind-email")
    public ResponseEntity<?> bindEmail(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");
        try {
            h5UserService.bindEmail(email, code);
            // 返回更新后的用户信息
            User updatedUser = h5UserService.getCurrentUser();
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "邮箱绑定成功",
                "user", updatedUser
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "绑定失败，请稍后重试"));
        }
    }

    /**
     * 记录用户登录设备信息
     * @param username 用户名
     * @param token 用户token
     */
    private void recordUserLoginDevice(String username, String token) {
        try {
            String deviceId = getDeviceFingerprint();
            if (deviceId != null) {
                String userDeviceKey = "user:device:" + username;
                String userTokenKey = "user:token:" + username;
                String deviceUserKey = "device:user:" + deviceId;
                
                // 记录设备信息，24小时过期
                stringRedisTemplate.opsForValue().set(userDeviceKey, deviceId, 24, TimeUnit.HOURS);
                stringRedisTemplate.opsForValue().set(userTokenKey, token, 24, TimeUnit.HOURS);
                stringRedisTemplate.opsForValue().set(deviceUserKey, username, 24, TimeUnit.HOURS);
                
                log.debug("记录用户 {} 登录设备信息，设备ID: {}", username, deviceId);
            }
        } catch (Exception e) {
            log.warn("记录用户设备信息失败: {}", e.getMessage());
        }
    }

    /**
     * 获取设备指纹
     * @return 设备指纹，如果不存在则返回null
     */
    private String getDeviceFingerprint() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String deviceId = request.getHeader("X-Device-ID");
                log.debug("获取设备指纹: {}", deviceId);
                return deviceId;
            }
        } catch (Exception e) {
            log.debug("无法获取设备指纹: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 清除用户设备登录记录
     * @param token 用户token
     */
    private void clearUserLoginDevice(String token) {
        try {
            String username = tokenProvider.getUsernameFromJWT(token);
            String deviceId = getDeviceFingerprint();
            
            if (username != null && deviceId != null) {
                String userDeviceKey = "user:device:" + username;
                String userTokenKey = "user:token:" + username;
                String deviceUserKey = "device:user:" + deviceId;
                
                // 清除设备记录
                stringRedisTemplate.delete(userDeviceKey);
                stringRedisTemplate.delete(userTokenKey);
                stringRedisTemplate.delete(deviceUserKey);
                
                log.debug("清除用户 {} 的设备登录记录，设备ID: {}", username, deviceId);
            }
        } catch (Exception e) {
            log.warn("清除用户设备信息失败: {}", e.getMessage());
        }
    }
} 