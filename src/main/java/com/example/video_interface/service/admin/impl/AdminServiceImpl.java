package com.example.video_interface.service.admin.impl;

import com.example.video_interface.dto.admin.AdminLoginRequest;
import com.example.video_interface.model.User;
import com.example.video_interface.repository.UserRepository;
import com.example.video_interface.security.JwtTokenProvider;
import com.example.video_interface.service.admin.IAdminService;
import com.example.video_interface.service.common.ILoginSecurityService;
import com.example.video_interface.util.RequestContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 管理员服务实现类
 * 实现管理员相关的业务逻辑，包括登录、登出、获取个人信息等操作
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements IAdminService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final ILoginSecurityService loginSecurityService;

    /**
     * 管理员登录
     * @param request 登录请求，包含用户名和密码
     * @return 登录成功的管理员信息
     * @throws IllegalArgumentException 如果用户名或密码错误或不是管理员
     */
    @Override
    public User adminLogin(AdminLoginRequest request) {
        log.info(" 处理管理员登录请求: {}", request.getUsername());
        
        // 获取客户端IP地址
        String clientIp = RequestContextUtil.getClientIpAddress();
        log.debug("客户端IP: {}", clientIp);

        // 查找用户（先查找用户，即使密码错误也要记录失败次数）
        User admin = null;
        try {
            admin = userRepository.findByUsernameAndRole(request.getUsername(), User.UserRole.ADMIN)
                    .orElse(null);
        } catch (Exception e) {
            log.error("查找管理员用户失败: {}", e.getMessage());
        }

        // 🔍 检查账户锁定状态（如果用户存在）
        if (admin != null) {
            boolean lockResult = loginSecurityService.isAccountLocked(admin.getUsername());
            if (lockResult) {
                String message = "账户已暂时锁定";
                throw new IllegalArgumentException(message);
            }
        }

        try {
            // 🔐 验证用户名和密码
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 再次确认是管理员账户（防止数据变更）
            if (admin == null) {
                admin = userRepository.findByUsernameAndRole(request.getUsername(), User.UserRole.ADMIN)
                        .orElseThrow(() -> new IllegalArgumentException("该账号不是管理员账号"));
            }
            
            // 检查账号状态
            if (!admin.isEnabled()) {
                log.warn("管理员账号已被禁用: {}", request.getUsername());
                throw new IllegalArgumentException("管理员账号已被禁用");
            }

            // ✅ 登录成功，记录成功日志并重置失败次数
            loginSecurityService.resetLoginFailures(admin.getUsername());
            
            // 更新最后登录时间和IP
            admin.setLastLoginTime(LocalDateTime.now());
            admin.setLastLoginIp(clientIp);
            
            log.info("管理员登录成功: {} (IP: {})", admin.getUsername(), clientIp);
            return userRepository.save(admin);
            
        } catch (Exception e) {
            log.error("管理员登录失败: {} (IP: {}) - 原因: {}", 
                request.getUsername(), clientIp, e.getMessage());
            
            // 🔍 构造详细的错误信息
            String errorMessage = "管理员登录失败";
            
            try {
                // 📝 记录登录失败
                boolean accountLocked = loginSecurityService.recordLoginFailure(admin.getUsername());
                
                // 如果是密码错误且用户存在，提供剩余尝试次数信息
                if (admin != null && e.getMessage().contains("Bad credentials")) {
                    // 重新查询用户以获取最新的失败次数
                    admin = userRepository.findByUsernameAndRole(request.getUsername(), User.UserRole.ADMIN)
                            .orElse(admin);
                    
                    int remainingAttempts = loginSecurityService.getRemainingAttempts(admin.getUsername());
                    if (remainingAttempts > 0 && !accountLocked) {
                        errorMessage = String.format("密码错误，您还有 %d 次尝试机会", remainingAttempts);
                    } else if (accountLocked) {
                        errorMessage = "登录失败次数过多，账户已被锁定";
                    } else {
                        errorMessage = "密码错误";
                    }
                } else {
                    // 其他错误
                    errorMessage = e.getMessage();
                }
            } catch (Exception recordException) {
                log.error("记录登录失败时出错: {}", recordException.getMessage());
                // 如果记录失败，使用通用错误消息
                if (admin != null && e.getMessage().contains("Bad credentials")) {
                    errorMessage = "密码错误";
                } else if (admin == null) {
                    errorMessage = "该账号不是管理员账号";
                } else {
                    errorMessage = e.getMessage();
                }
            }
            
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * 管理员登出
     * @param token JWT令牌
     */
    @Override
    public void adminLogout(String token) {
        try {
            // 将token加入Redis黑名单，设置过期时间与token剩余有效期一致
            String username = tokenProvider.getUsernameFromJWT(token);
            long expirationTime = tokenProvider.getExpirationDateFromToken(token).getTime();
            long currentTime = System.currentTimeMillis();
            long remainingTime = expirationTime - currentTime;
            
            if (remainingTime > 0) {
                String key = "blacklist:" + token;
                stringRedisTemplate.opsForValue().set(key, username, remainingTime, TimeUnit.MILLISECONDS);
                log.debug("Token已加入黑名单，管理员: {}", username);
            }
        } catch (Exception e) {
            log.error("管理员登出过程发生错误: {}", e.getMessage());
            throw new RuntimeException("退出登录失败");
        }
    }

    /**
     * 获取当前管理员信息
     * @return 当前管理员信息
     */
    @Override
    public User getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("管理员未登录");
        }

        User admin = userRepository.findByUsernameAndRole(authentication.getName(), User.UserRole.ADMIN)
                .orElseThrow(() -> new IllegalArgumentException("管理员账号不存在"));

        if (!admin.isEnabled()) {
            throw new IllegalStateException("管理员账号已被禁用");
        }

        return admin;
    }
} 