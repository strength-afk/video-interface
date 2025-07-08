package com.example.video_interface.service;

import com.example.video_interface.config.LoginSecurityConfig;
import com.example.video_interface.model.User;
import com.example.video_interface.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 🔒 登录安全服务
 * 处理登录失败次数、账户锁定、IP限制等安全功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginSecurityService {

    private final LoginSecurityConfig loginSecurityConfig;
    private final UserRepository userRepository;

    // IP失败次数缓存 - 使用内存缓存，重启后清零
    private final Map<String, IpFailureInfo> ipFailureCache = new ConcurrentHashMap<>();

    /**
     * IP失败信息
     */
    private static class IpFailureInfo {
        private int attempts;
        private LocalDateTime lastAttempt;
        private LocalDateTime lockTime;

        public IpFailureInfo() {
            this.attempts = 0;
            this.lastAttempt = LocalDateTime.now();
        }
    }

    /**
     * 🔍 检查账户是否被锁定
     * @param user 用户对象
     * @return 锁定检查结果
     */
    public LockCheckResult checkAccountLock(User user) {
        if (user == null) {
            return LockCheckResult.notLocked();
        }

        // 检查账户是否手动锁定
        if (Boolean.TRUE.equals(user.getIsLocked())) {
            // 检查是否到解锁时间
            if (user.getUnlockTime() != null && LocalDateTime.now().isAfter(user.getUnlockTime())) {
                // 自动解锁
                unlockAccount(user);
                return LockCheckResult.notLocked();
            }
            
            return LockCheckResult.locked(
                user.getLockReason() != null ? user.getLockReason() : "账户已被锁定",
                user.getUnlockTime()
            );
        }

        return LockCheckResult.notLocked();
    }

    /**
     * 🔍 检查IP是否被锁定
     * @param clientIp 客户端IP
     * @return 是否被锁定
     */
    public boolean isIpLocked(String clientIp) {
        if (!loginSecurityConfig.isEnableIpLocking() || clientIp == null) {
            return false;
        }

        IpFailureInfo ipInfo = ipFailureCache.get(clientIp);
        if (ipInfo == null) {
            return false;
        }

        // 检查是否达到IP锁定次数
        if (ipInfo.attempts >= loginSecurityConfig.getMaxIpFailedAttempts()) {
            // 检查锁定时间是否已过
            if (ipInfo.lockTime != null) {
                LocalDateTime unlockTime = ipInfo.lockTime.plusMinutes(loginSecurityConfig.getIpLockDurationMinutes());
                if (LocalDateTime.now().isAfter(unlockTime)) {
                    // 解锁IP
                    ipFailureCache.remove(clientIp);
                    log.info("🔓 IP自动解锁: {}", clientIp);
                    return false;
                }
                return true;
            }
        }

        return false;
    }

    /**
     * 🚨 记录登录失败
     * @param user 用户对象
     * @param clientIp 客户端IP
     * @return 是否导致账户锁定
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean recordLoginFailure(User user, String clientIp) {
        boolean accountLocked = false;

        if (user != null) {
            // 记录用户登录失败
            accountLocked = recordUserLoginFailure(user);
        }

        // 记录IP登录失败
        if (loginSecurityConfig.isEnableIpLocking() && clientIp != null) {
            recordIpLoginFailure(clientIp);
        }

        return accountLocked;
    }

    /**
     * 📝 记录用户登录失败
     * @param user 用户对象
     * @return 是否导致账户锁定
     */
    private boolean recordUserLoginFailure(User user) {
        log.debug("🔍 开始记录用户登录失败: {}", user.getUsername());
        
        // 增加失败次数
        int currentAttempts = user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0;
        log.debug("📊 当前失败次数: {}", currentAttempts);
        
        currentAttempts++;
        
        user.setFailedLoginAttempts(currentAttempts);
        user.setLastFailedLoginTime(LocalDateTime.now());

        boolean isAdmin = user.getRole() == User.UserRole.ADMIN;
        int maxAttempts = loginSecurityConfig.getMaxFailedAttempts(isAdmin);

        log.warn("🚨 用户登录失败: {} ({}), 失败次数: {}/{}", 
            user.getUsername(), isAdmin ? "管理员" : "普通用户", currentAttempts, maxAttempts);

        // 检查是否需要锁定账户
        if (currentAttempts >= maxAttempts) {
            lockAccount(user, "连续登录失败次数过多", isAdmin);
            
            if (loginSecurityConfig.isEnableLockNotification()) {
                log.error("🔒 账户已锁定: {} (失败次数: {})", user.getUsername(), currentAttempts);
                // TODO: 发送锁定通知邮件/短信
            }
            
            User savedUser = userRepository.save(user);
            log.info("💾 账户锁定状态已保存: {} (失败次数: {}, 是否锁定: {})", 
                savedUser.getUsername(), savedUser.getFailedLoginAttempts(), savedUser.getIsLocked());
            return true;
        }

        User savedUser = userRepository.save(user);
        log.info("💾 失败次数已保存: {} (失败次数: {})", 
            savedUser.getUsername(), savedUser.getFailedLoginAttempts());
        return false;
    }

    /**
     * 📝 记录IP登录失败
     * @param clientIp 客户端IP
     */
    private void recordIpLoginFailure(String clientIp) {
        IpFailureInfo ipInfo = ipFailureCache.computeIfAbsent(clientIp, k -> new IpFailureInfo());
        
        ipInfo.attempts++;
        ipInfo.lastAttempt = LocalDateTime.now();

        // 检查是否需要锁定IP
        if (ipInfo.attempts >= loginSecurityConfig.getMaxIpFailedAttempts()) {
            ipInfo.lockTime = LocalDateTime.now();
            log.warn("🔒 IP已锁定: {} (失败次数: {})", clientIp, ipInfo.attempts);
        }

        log.debug("📊 IP失败统计: {} - 失败次数: {}", clientIp, ipInfo.attempts);
    }

    /**
     * ✅ 记录登录成功
     * @param user 用户对象
     * @param clientIp 客户端IP
     */
    @Transactional
    public void recordLoginSuccess(User user, String clientIp) {
        if (user != null) {
            // 重置用户失败次数
            user.setFailedLoginAttempts(0);
            user.setLastFailedLoginTime(null);
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(clientIp);
            
            userRepository.save(user);
            
            log.info("✅ 用户登录成功: {} (IP: {})", user.getUsername(), clientIp);
        }

        // 清除IP失败记录
        if (clientIp != null && ipFailureCache.containsKey(clientIp)) {
            ipFailureCache.remove(clientIp);
            log.debug("🧹 清除IP失败记录: {}", clientIp);
        }
    }

    /**
     * 🔒 锁定账户
     * @param user 用户对象
     * @param reason 锁定原因
     * @param isAdmin 是否为管理员
     */
    private void lockAccount(User user, String reason, boolean isAdmin) {
        user.setIsLocked(true);
        user.setLockReason(reason);
        user.setLockTime(LocalDateTime.now());
        
        int lockDuration = loginSecurityConfig.getLockDurationMinutes(isAdmin);
        user.setUnlockTime(LocalDateTime.now().plusMinutes(lockDuration));
        
        log.warn("🔒 账户锁定: {} - 原因: {} - 解锁时间: {}", 
            user.getUsername(), reason, user.getUnlockTime());
    }

    /**
     * 🔓 解锁账户
     * @param user 用户对象
     */
    @Transactional
    public void unlockAccount(User user) {
        user.setIsLocked(false);
        user.setLockReason(null);
        user.setLockTime(null);
        user.setUnlockTime(null);
        user.setFailedLoginAttempts(0);
        user.setLastFailedLoginTime(null);
        
        userRepository.save(user);
        
        log.info("🔓 账户解锁: {}", user.getUsername());
    }

    /**
     * 📊 获取用户剩余尝试次数
     * @param user 用户对象
     * @return 剩余尝试次数
     */
    public int getRemainingAttempts(User user) {
        if (user == null) {
            return 0;
        }
        
        boolean isAdmin = user.getRole() == User.UserRole.ADMIN;
        int maxAttempts = loginSecurityConfig.getMaxFailedAttempts(isAdmin);
        int currentAttempts = user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0;
        
        return Math.max(0, maxAttempts - currentAttempts);
    }

    /**
     * 🔍 锁定检查结果
     */
    public static class LockCheckResult {
        private final boolean locked;
        private final String reason;
        private final LocalDateTime unlockTime;

        private LockCheckResult(boolean locked, String reason, LocalDateTime unlockTime) {
            this.locked = locked;
            this.reason = reason;
            this.unlockTime = unlockTime;
        }

        public static LockCheckResult locked(String reason, LocalDateTime unlockTime) {
            return new LockCheckResult(true, reason, unlockTime);
        }

        public static LockCheckResult notLocked() {
            return new LockCheckResult(false, null, null);
        }

        // Getters
        public boolean isLocked() { return locked; }
        public String getReason() { return reason; }
        public LocalDateTime getUnlockTime() { return unlockTime; }
    }
} 