package com.example.video_interface.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户统计信息DTO
 * 用于管理员查看用户相关统计数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatistics {
    /**
     * 总用户数
     */
    private Long totalUsers;

    /**
     * VIP用户数
     */
    private Long vipUsers;

    /**
     * 锁定用户数
     */
    private Long lockedUsers;

    /**
     * 今日新增用户数
     */
    private Long todayNewUsers;

    /**
     * 本周新增用户数
     */
    private Long weekNewUsers;

    /**
     * 本月新增用户数
     */
    private Long monthNewUsers;

    /**
     * 今日活跃用户数
     */
    private Long todayActiveUsers;

    /**
     * 本周活跃用户数
     */
    private Long weekActiveUsers;

    /**
     * 本月活跃用户数
     */
    private Long monthActiveUsers;

    /**
     * 管理员用户数
     */
    private Long adminUsers;

    /**
     * 普通用户数
     */
    private Long normalUsers;
} 