package com.example.video_interface.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * 🔒 登录安全配置类
 * 管理登录失败次数控制、账户锁定等安全参数
 * 支持通过配置文件动态调整，提高系统安全性
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "app.security.login")
@Data
public class LoginSecurityConfig {

    // 🚨 登录失败次数控制
    /**
     * 最大登录失败次数（普通用户）
     * 默认5次，可通过配置文件调整
     */
    private int maxFailedAttempts = 10;

    /**
     * 管理员最大登录失败次数
     * 默认3次，管理员账户安全要求更高
     */
    private int adminMaxFailedAttempts = 3;

    // ⏰ 锁定时间配置
    /**
     * 账户锁定时间（分钟）
     * 默认30分钟
     */
    private int lockDurationMinutes = 30;

    /**
     * 管理员账户锁定时间（分钟）
     * 默认60分钟，管理员账户锁定时间更长
     */
    private int adminLockDurationMinutes = 60;

    // 🔄 重置配置
    /**
     * 失败次数重置时间窗口（小时）
     * 如果在此时间内没有失败登录，重置失败次数
     * 默认24小时
     */
    private int resetWindowHours = 24;



    // 📧 通知配置
    /**
     * 是否启用锁定通知
     * 默认启用
     */
    private boolean enableLockNotification = true;

    /**
     * 是否启用异常登录通知
     * 默认启用
     */
    private boolean enableSuspiciousLoginNotification = true;

    // 🔍 监控配置
    /**
     * 是否启用登录日志记录
     * 默认启用
     */
    private boolean enableLoginLogging = true;

    /**
     * 是否启用详细的安全日志
     * 默认启用
     */
    private boolean enableDetailedSecurityLogging = true;

    @PostConstruct
    public void init() {
        log.info(" 登录安全配置初始化完成:");
        log.info("  ├─ 普通用户最大失败次数: {}", maxFailedAttempts);
        log.info("  ├─ 管理员最大失败次数: {}", adminMaxFailedAttempts);
        log.info("  ├─ 普通用户锁定时间: {} 分钟", lockDurationMinutes);
        log.info("  ├─ 管理员锁定时间: {} 分钟", adminLockDurationMinutes);
        log.info("  ├─ 失败次数重置窗口: {} 小时", resetWindowHours);

        log.info("  ├─ 锁定通知: {}", enableLockNotification ? "启用" : "禁用");
        log.info("  └─ 登录日志: {}", enableLoginLogging ? "启用" : "禁用");

        // 验证配置合理性
        validateConfiguration();
    }

    /**
     * 验证配置的合理性
     */
    private void validateConfiguration() {
        if (maxFailedAttempts <= 0 || adminMaxFailedAttempts <= 0) {
            log.warn(" 最大失败次数不能小于等于0，将使用默认值");
            if (maxFailedAttempts <= 0) maxFailedAttempts = 5;
            if (adminMaxFailedAttempts <= 0) adminMaxFailedAttempts = 3;
        }

        if (lockDurationMinutes <= 0 || adminLockDurationMinutes <= 0) {
            log.warn(" 锁定时间不能小于等于0，将使用默认值");
            if (lockDurationMinutes <= 0) lockDurationMinutes = 30;
            if (adminLockDurationMinutes <= 0) adminLockDurationMinutes = 60;
        }

        if (adminMaxFailedAttempts > maxFailedAttempts) {
            log.warn(" 管理员最大失败次数应该小于等于普通用户，建议调整配置");
        }

        if (adminLockDurationMinutes < lockDurationMinutes) {
            log.warn(" 管理员锁定时间建议大于等于普通用户锁定时间");
        }

        log.info(" 登录安全配置验证完成");
    }

    /**
     * 获取指定角色的最大失败次数
     * @param isAdmin 是否为管理员
     * @return 最大失败次数
     */
    public int getMaxFailedAttempts(boolean isAdmin) {
        return isAdmin ? adminMaxFailedAttempts : maxFailedAttempts;
    }

    /**
     * 获取指定角色的锁定时间（分钟）
     * @param isAdmin 是否为管理员
     * @return 锁定时间（分钟）
     */
    public int getLockDurationMinutes(boolean isAdmin) {
        return isAdmin ? adminLockDurationMinutes : lockDurationMinutes;
    }
} 