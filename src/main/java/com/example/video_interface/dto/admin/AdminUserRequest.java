package com.example.video_interface.dto.admin;

import com.example.video_interface.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理员用户管理请求DTO
 * 用于管理员对用户进行各种操作
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserRequest {
    /**
     * 用户ID（用于更新操作）
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 用户头像URL
     */
    private String avatar;

    /**
     * 是否为VIP用户
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
     * 账户是否被锁定
     */
    private Boolean isLocked;

    /**
     * 账户锁定原因
     */
    private String lockReason;

    /**
     * 连续登录失败次数
     */
    private Integer failedLoginAttempts;

    /**
     * 账户锁定时间
     */
    private LocalDateTime lockTime;

    /**
     * 账户自动解锁时间
     */
    private LocalDateTime unlockTime;

    /**
     * 用户状态
     */
    private User.UserStatus status;

    /**
     * 用户角色
     */
    private User.UserRole role;

    /**
     * 查询参数：页码
     */
    private Integer page = 1;

    /**
     * 查询参数：每页大小
     */
    private Integer size = 10;

    /**
     * 查询参数：搜索关键词（用户名、邮箱、手机号）
     */
    private String keyword;

    /**
     * 查询参数：用户状态筛选
     */
    private User.UserStatus statusFilter;

    /**
     * 查询参数：用户角色筛选
     */
    private User.UserRole roleFilter;

    /**
     * 查询参数：VIP状态筛选
     */
    private Boolean vipFilter;

    /**
     * 查询参数：锁定状态筛选
     */
    private Boolean lockedFilter;

    /**
     * 查询参数：开始时间
     */
    private LocalDateTime startTime;

    /**
     * 查询参数：结束时间
     */
    private LocalDateTime endTime;

    /**
     * 排序字段
     */
    private String sortBy = "createdAt";

    /**
     * 排序方向：asc-升序，desc-降序
     */
    private String sortDirection = "desc";
} 