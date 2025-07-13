package com.example.video_interface.dto.admin;

import com.example.video_interface.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理员用户DTO
 * 用于管理员管理功能的数据传输
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDTO {
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phoneNumber;
    
    /**
     * 用户角色
     */
    private String role;
    
    /**
     * 用户状态
     */
    private String status;
    
    /**
     * 是否为VIP
     */
    private Boolean isVip;
    
    /**
     * VIP过期时间
     */
    private LocalDateTime vipExpireTime;
    
    /**
     * 账户余额
     */
    private BigDecimal accountBalance;
    
    /**
     * 观看时长（分钟）
     */
    private Long watchTime;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 最后登录IP
     */
    private String lastLoginIp;
    
    /**
     * 是否被锁定
     */
    private Boolean isLocked;
    
    /**
     * 锁定原因
     */
    private String lockReason;
    
    /**
     * 登录失败次数
     */
    private Integer failedLoginAttempts;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 从User实体转换为DTO
     */
    public static AdminUserDTO fromUser(User user) {
        return AdminUserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .isVip(user.getIsVip())
                .vipExpireTime(user.getVipExpireTime())
                .accountBalance(user.getAccountBalance())
                .watchTime(user.getWatchTime())
                .lastLoginTime(user.getLastLoginTime())
                .lastLoginIp(user.getLastLoginIp())
                .isLocked(user.getIsLocked())
                .lockReason(user.getLockReason())
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
} 