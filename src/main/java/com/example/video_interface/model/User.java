package com.example.video_interface.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * 用户实体类
 * 存储用户基本信息、账户状态、VIP信息等
 */
@Data
@Entity
@Table(name = "users")
@Comment("用户信息表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    /**
     * 用户状态枚举
     * 用于标识用户当前状态
     */
    public enum UserStatus {
        ACTIVE,     // 正常状态
        INACTIVE,   // 未激活状态
        LOCKED,     // 账户被锁定
        DELETED;    // 账户已删除
        
        @com.fasterxml.jackson.annotation.JsonValue
        public String getValue() {
            return this.name();
        }
    }

    /**
     * 用户角色枚举
     * 用于区分用户权限级别
     */
    public enum UserRole {
        ADMIN,      // 管理员，具有最高权限
        USER,       // 普通用户，具有基本权限
        VIP;        // VIP用户，具有特殊权限
        
        @com.fasterxml.jackson.annotation.JsonValue
        public String getValue() {
            return this.name();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("用户ID，主键，自增")
    private Long id;

    @Column(unique = true, nullable = false)
    @Comment("用户名，唯一，不可为空")
    private String username;

    @Column(nullable = false)
    @Comment("密码，加密存储，不可为空")
    private String password;

    @Column(unique = true, nullable = true)
    @Comment("邮箱地址，唯一，可为空")
    private String email;

    @Column(name = "phone_number", nullable = true)
    @Comment("手机号码，最大长度20，可为空")
    private String phoneNumber;

    @Column(length = 200, nullable = true)
    @Comment("用户头像URL，最大长度200，可为空")
    private String avatar;

    @Column(name = "is_vip")
    @Comment("是否为VIP用户")
    private Boolean isVip = false;

    @Column(name = "vip_expire_time")
    @Comment("VIP过期时间")
    private LocalDateTime vipExpireTime;

    @Column(name = "account_balance", precision = 10, scale = 2)
    @Comment("账户余额")
    private BigDecimal accountBalance = BigDecimal.ZERO;

    @Column(name = "watch_time")
    @Comment("观看时长（分钟）")
    private Long watchTime = 0L;

    @Column(name = "last_login_time")
    @Comment("最后登录时间")
    private LocalDateTime lastLoginTime;

    @Column(name = "last_login_ip", length = 50)
    @Comment("最后登录IP地址")
    private String lastLoginIp;

    @Column(name = "is_locked")
    @Comment("账户是否被锁定")
    private Boolean isLocked = false;

    @Column(name = "lock_reason", length = 200)
    @Comment("账户锁定原因")
    private String lockReason;

    @Column(name = "failed_login_attempts")
    @Comment("连续登录失败次数")
    private Integer failedLoginAttempts = 0;

    @Column(name = "last_failed_login_time")
    @Comment("最后一次登录失败时间")
    private LocalDateTime lastFailedLoginTime;

    @Column(name = "lock_time")
    @Comment("账户锁定时间")
    private LocalDateTime lockTime;

    @Column(name = "unlock_time")
    @Comment("账户自动解锁时间")
    private LocalDateTime unlockTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @Comment("创建时间")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Comment("最后更新时间")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("用户状态：ACTIVE-正常，INACTIVE-未激活，LOCKED-锁定，DELETED-已删除")
    private UserStatus status = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("用户角色：ADMIN-管理员，USER-普通用户，VIP-VIP用户")
    private UserRole role = UserRole.USER;

    public LocalDateTime getVipExpireAt() {
        return this.vipExpireTime;
    }
    public void setVipExpireAt(LocalDateTime vipExpireAt) {
        this.vipExpireTime = vipExpireAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isVip == null) isVip = false;
        if (isLocked == null) isLocked = false;
        if (accountBalance == null) accountBalance = BigDecimal.ZERO;
        if (watchTime == null) watchTime = 0L;
        if (role == null) role = UserRole.USER;
        if (status == null) status = UserStatus.ACTIVE;
        if (failedLoginAttempts == null) failedLoginAttempts = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
} 