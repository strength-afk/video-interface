package com.example.video_interface.service.h5.impl;

import com.example.video_interface.dto.h5.H5LoginRequest;
import com.example.video_interface.dto.h5.H5RegisterRequest;
import com.example.video_interface.model.User;
import com.example.video_interface.repository.UserRepository;
import com.example.video_interface.security.JwtTokenProvider;
import com.example.video_interface.service.common.ICaptchaService;
import com.example.video_interface.service.common.IH5LoginFailureService;
import com.example.video_interface.service.common.IRegistrationLimitService;
import com.example.video_interface.service.h5.IH5UserService;
import com.example.video_interface.util.RequestContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;



/**
 * H5用户服务实现类
 * 实现H5端用户相关的业务逻辑，包括注册、登录、信息管理等操作
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class H5UserServiceImpl implements IH5UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final ICaptchaService captchaService;
    private final IRegistrationLimitService registrationLimitService;
    private final IH5LoginFailureService h5LoginFailureService;

    /**
     * 用户注册
     * @param request 注册请求，包含用户名和密码
     * @return 注册成功的用户信息
     * @throws IllegalArgumentException 如果用户名已存在或参数无效
     */
    @Override
    @Transactional
    public User registerUser(H5RegisterRequest request) {
        log.info("开始处理用户注册请求: {}", request.getUsername());

        // 获取客户端IP地址
        String clientIp = RequestContextUtil.getClientIpAddress();
        log.debug("客户端IP: {}", clientIp);

        // 验证请求参数
        validateRegisterRequest(request);

        // 检查IP注册限制
        if (!registrationLimitService.canRegister(clientIp)) {
            int currentCount = registrationLimitService.getCurrentCount(clientIp);
            long resetTime = registrationLimitService.getResetTimeInSeconds(clientIp);
            int resetHours = (int) Math.ceil(resetTime / 3600.0);
            
            log.warn("IP {} 注册次数已达限制: {}/3，需等待 {} 小时后重试", 
                clientIp, currentCount, resetHours);
            throw new IllegalArgumentException("注册频繁，请稍后重试");
        }

        try {
            // 创建新用户
            User user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .status(User.UserStatus.ACTIVE)
                    .role(User.UserRole.USER)
                    .build();

            log.info("保存新用户到数据库");
            User savedUser = userRepository.save(user);
            
            // 记录注册次数
            int newCount = registrationLimitService.recordRegistration(clientIp);
            log.info("用户注册成功: {} (IP: {}), 该IP已注册 {}/3 个账号", 
                savedUser.getUsername(), clientIp, newCount);
            
            return savedUser;
        } catch (Exception e) {
            log.error("用户注册失败: {}", e.getMessage(), e);
            throw new RuntimeException("注册失败，请稍后重试");
        }
    }

    /**
     * 验证注册请求参数
     * @param request 注册请求
     * @throws IllegalArgumentException 如果参数无效
     */
    private void validateRegisterRequest(H5RegisterRequest request) {
        // 验证用户名
        if (!StringUtils.hasText(request.getUsername())) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (request.getUsername().length() < 3 || request.getUsername().length() > 20) {
            throw new IllegalArgumentException("用户名长度必须在3-20个字符之间");
        }
        if (!request.getUsername().matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("用户名只能包含字母、数字和下划线");
        }

        // 验证密码
        if (!StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (request.getPassword().length() < 6 || request.getPassword().length() > 20) {
            throw new IllegalArgumentException("密码长度必须在6-20个字符之间");
        }

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("用户名已存在: {}", request.getUsername());
            throw new IllegalArgumentException("用户名已被使用");
        }
    }

    /**
     * H5用户登录
     * @param request 登录请求，包含用户名、密码、验证码等信息
     * @return 登录成功的用户信息
     * @throws IllegalArgumentException 如果用户名或密码错误或验证码错误
     */
    @Override
    public Map<String, Object> h5Login(H5LoginRequest request) {
        log.info("处理H5用户登录请求: {}", request.getUsername());
        
        // 获取客户端IP地址
        String clientIp = RequestContextUtil.getClientIpAddress();
        log.debug("客户端IP: {}", clientIp);

        try {
            // 检查是否需要验证码
            boolean needCaptcha = h5LoginFailureService.needCaptcha(clientIp);
            
            // 如果需要验证码但没有提供，抛出异常
            if (needCaptcha && (request.getCaptcha() == null || request.getSessionId() == null)) {
                log.warn("需要验证码但未提供: {} (IP: {})", request.getUsername(), clientIp);
                throw new IllegalArgumentException("请输入验证码");
            }

            // 验证码检查（如果提供了验证码信息）
            if (request.getCaptcha() != null && request.getSessionId() != null) {
                boolean captchaValid = captchaService.verifyCaptcha(request.getSessionId(), request.getCaptcha());
                if (!captchaValid) {
                    log.warn("验证码验证失败: {} (IP: {})", request.getUsername(), clientIp);
                    throw new IllegalArgumentException("验证码错误或已过期");
                }
                log.debug("验证码验证通过: {} (IP: {})", request.getUsername(), clientIp);
            }

            // 验证用户名和密码
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 获取用户信息
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
            
            // 检查账号状态
            if (!user.isEnabled()) {
                log.warn("用户账户已被禁用: {}", request.getUsername());
                throw new IllegalArgumentException("账户已被禁用");
            }

            // 获取设备指纹
            String deviceId = getDeviceFingerprint();
            log.debug("用户 {} 登录设备ID: {}", request.getUsername(), deviceId);

            // 处理单设备登录冲突（自动顶掉其他设备）
            boolean kickedOtherDevice = handleDeviceConflict(user.getUsername(), deviceId);
            if (kickedOtherDevice) {
                log.info("用户 {} 登录时顶掉了其他设备的会话", user.getUsername());
            }

            // 登录成功，更新最后登录时间和IP
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(clientIp);
            user = userRepository.save(user);
            
            // 重置登录失败次数
            h5LoginFailureService.resetLoginFailures(clientIp);
            
            log.info("H5用户登录成功: {} (IP: {}, 设备: {})", user.getUsername(), clientIp, deviceId);
            
            // 返回用户信息和needCaptcha状态
            return Map.of(
                "user", user,
                "needCaptcha", false,
                "kickedOtherDevice", kickedOtherDevice
            );
            

        } catch (Exception e) {
            log.error("H5用户登录失败: {} (IP: {}) - 原因: {}", 
                request.getUsername(), clientIp, e.getMessage());
            
            // 记录登录失败并检查是否需要验证码
            boolean needCaptcha = h5LoginFailureService.recordLoginFailure(clientIp);
            
            // H5端简化错误处理，不记录失败次数，不锁定账户
            String errorMessage;
            
            if (e.getMessage().contains("验证码")) {
                errorMessage = e.getMessage(); // 保持验证码错误信息
            } else if (e.getMessage().contains("Bad credentials")) {
                errorMessage = "密码错误，请重试";
            } else if (e.getMessage().contains("User not found") || e.getMessage().contains("用户不存在")) {
                errorMessage = "用户名不存在";
            } else if (e.getMessage().contains("User is disabled") || e.getMessage().contains("账户已被禁用")) {
                errorMessage = "账户已被禁用";
            } else if (e.getMessage().contains("User account is locked")) {
                errorMessage = "账号被锁定";
            } else {
                errorMessage = "登录失败，请重试";
            }
            
            log.warn("H5端错误信息: {}", errorMessage);
            
            // 直接抛出包含错误信息的异常
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * 用户登出
     * @param token JWT令牌
     */
    @Override
    public void logout(String token) {
        try {
            // 将token加入Redis黑名单，设置过期时间与token剩余有效期一致
            String username = tokenProvider.getUsernameFromJWT(token);
            long expirationTime = tokenProvider.getExpirationDateFromToken(token).getTime();
            long currentTime = System.currentTimeMillis();
            long remainingTime = expirationTime - currentTime;
            
            if (remainingTime > 0) {
                String key = "blacklist:" + token;
                stringRedisTemplate.opsForValue().set(key, username, remainingTime, TimeUnit.MILLISECONDS);
                log.debug("Token已加入黑名单，用户: {}", username);
            }
        } catch (Exception e) {
            log.error("登出过程发生错误: {}", e.getMessage());
            throw new RuntimeException("退出登录失败");
        }
    }

    /**
     * 获取当前用户信息
     * @return 当前用户信息
     */
    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("用户未登录");
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    /**
     * 更新当前用户信息
     * @param userData 用户信息更新数据
     * @return 更新后的用户信息
     */
    @Override
    @Transactional
    public User updateCurrentUser(Map<String, String> userData) {
        User currentUser = getCurrentUser();

        // 更新用户信息
        if (userData.containsKey("avatar")) {
            currentUser.setAvatar(userData.get("avatar"));
        }
        if (userData.containsKey("phoneNumber")) {
            currentUser.setPhoneNumber(userData.get("phoneNumber"));
        }
        if (userData.containsKey("email")) {
            String newEmail = userData.get("email");
            if (!newEmail.equals(currentUser.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("邮箱已被使用");
            }
            currentUser.setEmail(newEmail);
        }

        return userRepository.save(currentUser);
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
     * 处理设备登录冲突，自动顶掉其他设备
     * @param username 用户名
     * @param currentDeviceId 当前设备ID
     * @return 如果顶掉了其他设备返回true，否则返回false
     */
    private boolean handleDeviceConflict(String username, String currentDeviceId) {
        if (currentDeviceId == null) {
            log.debug("设备ID为空，跳过设备冲突检查");
            return false;
        }

        String userDeviceKey = "user:device:" + username;
        String userTokenKey = "user:token:" + username;
        String deviceUserKey = "device:user:" + currentDeviceId;
        
        // 获取用户当前登录的设备
        String oldDevice = stringRedisTemplate.opsForValue().get(userDeviceKey);
        
        if (oldDevice == null) {
            log.debug("用户 {} 未在任何设备登录", username);
            return false;
        }
        
        if (oldDevice.equals(currentDeviceId)) {
            log.debug("用户 {} 已在当前设备登录", username);
            return false;
        }
        
        // 顶掉其他设备
        log.info("用户 {} 在新设备登录，顶掉原设备: {} -> {}", username, oldDevice, currentDeviceId);
        
        // 将旧设备的token加入黑名单
        String oldToken = stringRedisTemplate.opsForValue().get(userTokenKey);
        if (oldToken != null) {
            String blacklistKey = "blacklist:" + oldToken;
            stringRedisTemplate.opsForValue().set(blacklistKey, username, 24, TimeUnit.HOURS);
            log.debug("将用户 {} 的旧token加入黑名单", username);
        }
        
        // 清除旧设备记录
        String oldDeviceUserKey = "device:user:" + oldDevice;
        stringRedisTemplate.delete(oldDeviceUserKey);
        
        return true;
    }
} 