package com.example.video_interface.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.Set;

/**
 * 🔐 加密配置管理类
 * 统一管理加密相关的配置参数、密钥、算法等
 * 确保整个系统的加密配置一致性和安全性
 */
@Slf4j
@Configuration
@Getter
public class CryptoConfig {

    // 🔧 基础配置
    @Value("${app.crypto.base-secret:K9mN7pQ2vX8bE4wR6jL3nA5sD9gH2kP7uY1tI6oE8rQ4mN9vX3bK7sA2wE5gL8pU}")
    private String baseSecret;

    @Value("${app.crypto.time-window:180000}")
    private long timeWindow; // 3分钟时间窗口，与前端保持一致

    @Value("${app.crypto.device-salt:R7mK3nP9wE6bA2sD4vX8jL5oI1uY7tQ9}")
    private String deviceSalt;

    // 🔐 加密算法配置
    @Value("${app.crypto.aes.algorithm:AES}")
    private String aesAlgorithm;

    @Value("${app.crypto.aes.transformation:AES/CTR/NoPadding}")
    private String aesTransformation;

    @Value("${app.crypto.aes.key-size:256}")
    private int aesKeySize;

    @Value("${app.crypto.aes.iv-size:128}")
    private int aesIvSize;

    // 🔑 HMAC配置
    @Value("${app.crypto.hmac.algorithm:HmacSHA256}")
    private String hmacAlgorithm;

    // 🔍 哈希算法配置
    @Value("${app.crypto.hash.algorithm:SHA-256}")
    private String hashAlgorithm;

    // 🛡️ JWT增强配置
    @Value("${app.crypto.jwt.device-binding:true}")
    private boolean jwtDeviceBinding;

    @Value("${app.crypto.jwt.additional-encryption:true}")
    private boolean jwtAdditionalEncryption;

    @Value("${app.crypto.jwt.aes-transformation:AES/ECB/PKCS5Padding}")
    private String jwtAesTransformation;

    // 📱 客户端配置
    @Value("${app.crypto.client.h5.enabled:true}")
    private boolean h5CryptoEnabled;

    @Value("${app.crypto.client.admin.enabled:true}")
    private boolean adminCryptoEnabled;

    @Value("${app.crypto.client.mobile.enabled:true}")
    private boolean mobileCryptoEnabled;

    // 🚨 安全策略配置
    @Value("${app.crypto.security.max-time-drift:60000}")
    private long maxTimeDrift; // 最大时间偏移（1分钟）

    @Value("${app.crypto.security.require-signature:true}")
    private boolean requireSignature;

    @Value("${app.crypto.security.strict-device-binding:false}")
    private boolean strictDeviceBinding;

    // 📋 敏感字段配置
    private Set<String> sensitiveFields;

    @PostConstruct
    public void init() {
        // 初始化敏感字段列表
        this.sensitiveFields = Set.of(
            "password", "oldPassword", "newPassword", "confirmPassword",
            "token", "accessToken", "refreshToken", "authToken",
            "username", "email", "phone", "idCard", 
            "creditCard", "bankAccount", "paymentInfo"
        );

        log.info(" 加密配置初始化完成:");
        log.info("  ├─ 基础密钥长度: {} 字符", baseSecret.length());
        log.info("  ├─ 时间窗口: {} ms", timeWindow);
        log.info("  ├─ AES算法: {}", aesTransformation);
        log.info("  ├─ HMAC算法: {}", hmacAlgorithm);
        log.info("  ├─ 哈希算法: {}", hashAlgorithm);
        log.info("  ├─ JWT设备绑定: {}", jwtDeviceBinding);
        log.info("  ├─ JWT额外加密: {}", jwtAdditionalEncryption);
        log.info("  ├─ H5加密启用: {}", h5CryptoEnabled);
        log.info("  ├─ 管理端加密启用: {}", adminCryptoEnabled);
        log.info("  ├─ 移动端加密启用: {}", mobileCryptoEnabled);
        log.info("  ├─ 要求签名验证: {}", requireSignature);
        log.info("  ├─ 严格设备绑定: {}", strictDeviceBinding);
        log.info("  └─ 敏感字段数量: {}", sensitiveFields.size());

        // 安全性检查
        validateConfiguration();
    }

    /**
     * 验证配置的安全性
     */
    private void validateConfiguration() {
        // 检查基础密钥强度
        if (baseSecret.length() < 32) {
            log.warn(" 基础密钥长度不足32字符，建议使用更强的密钥");
        }

        // 检查时间窗口设置
        if (timeWindow > 600000) { // 超过10分钟
            log.warn(" 时间窗口设置过长({}ms)，可能存在安全风险", timeWindow);
        }

        if (timeWindow < 60000) { // 少于1分钟
            log.warn(" 时间窗口设置过短({}ms)，可能影响用户体验", timeWindow);
        }

        // 检查AES密钥大小
        if (aesKeySize < 256) {
            log.warn(" AES密钥长度不足256位，建议使用AES-256");
        }

        log.info(" 加密配置安全性检查完成");
    }

    /**
     * 获取客户端类型的加密启用状态
     * @param clientType 客户端类型 (h5/admin/mobile)
     * @return 是否启用加密
     */
    public boolean isCryptoEnabledForClient(String clientType) {
        if (clientType == null) {
            return true; // 默认启用
        }

        return switch (clientType.toLowerCase()) {
            case "h5" -> h5CryptoEnabled;
            case "admin" -> adminCryptoEnabled;
            case "mobile" -> mobileCryptoEnabled;
            default -> true; // 未知客户端默认启用
        };
    }

    /**
     * 检查字段是否为敏感字段
     * @param fieldName 字段名
     * @return 是否为敏感字段
     */
    public boolean isSensitiveField(String fieldName) {
        return sensitiveFields.contains(fieldName);
    }

    /**
     * 获取算法配置摘要（用于日志和调试）
     * @return 配置摘要
     */
    public String getConfigSummary() {
        return String.format(
            "CryptoConfig{aes=%s, hmac=%s, hash=%s, timeWindow=%dms, jwtBinding=%b}",
            aesTransformation, hmacAlgorithm, hashAlgorithm, timeWindow, jwtDeviceBinding
        );
    }

    /**
     * 验证时间戳是否在允许的时间窗口内
     * @param timestamp 要验证的时间戳
     * @return 是否有效
     */
    public boolean isTimestampValid(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long timeDiff = Math.abs(currentTime - timestamp);
        return timeDiff <= (timeWindow + maxTimeDrift);
    }

    /**
     * 获取密钥强度等级
     * @return 密钥强度等级 (WEAK/MEDIUM/STRONG)
     */
    public String getKeyStrengthLevel() {
        int secretLength = baseSecret.length();
        if (secretLength < 16) {
            return "WEAK";
        } else if (secretLength < 32) {
            return "MEDIUM";
        } else {
            return "STRONG";
        }
    }

    /**
     * 是否启用严格模式（所有安全特性都开启）
     * @return 是否启用严格模式
     */
    public boolean isStrictMode() {
        return requireSignature && jwtDeviceBinding && jwtAdditionalEncryption && strictDeviceBinding;
    }
} 