package com.example.video_interface.service.admin;

import com.example.video_interface.dto.admin.AdminUserDTO;
import com.example.video_interface.dto.admin.AdminUserRequest;
import com.example.video_interface.dto.admin.UserStatistics;
import com.example.video_interface.model.User;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理员用户管理服务接口
 * 提供用户查询、更新、删除等管理功能
 */
public interface IAdminUserService {
    /**
     * 分页查询用户列表
     * @param request 查询请求，包含分页、筛选、排序参数
     * @return 分页用户列表
     */
    Page<AdminUserDTO> getUserList(AdminUserRequest request);

    /**
     * 根据用户ID获取用户详情
     * @param userId 用户ID
     * @return 用户详情
     * @throws IllegalArgumentException 如果用户不存在
     */
    AdminUserDTO getUserById(Long userId);

    /**
     * 更新用户信息
     * @param request 用户更新请求
     * @return 更新后的用户信息
     * @throws IllegalArgumentException 如果用户不存在或更新失败
     */
    AdminUserDTO updateUser(AdminUserRequest request);

    /**
     * 删除用户
     * @param userId 用户ID
     * @throws IllegalArgumentException 如果用户不存在或删除失败
     */
    void deleteUser(Long userId);

    /**
     * 锁定用户账户
     * @param userId 用户ID
     * @param reason 锁定原因
     * @param lockDuration 锁定时长（分钟），null表示永久锁定
     * @return 锁定后的用户信息
     * @throws IllegalArgumentException 如果用户不存在或锁定失败
     */
    AdminUserDTO lockUser(Long userId, String reason, Integer lockDuration);

    /**
     * 解锁用户账户
     * @param userId 用户ID
     * @return 解锁后的用户信息
     * @throws IllegalArgumentException 如果用户不存在或解锁失败
     */
    AdminUserDTO unlockUser(Long userId);

    /**
     * 重置用户登录失败次数
     * @param userId 用户ID
     * @return 重置后的用户信息
     * @throws IllegalArgumentException 如果用户不存在
     */
    AdminUserDTO resetLoginAttempts(Long userId);

    /**
     * 设置用户VIP状态
     * @param userId 用户ID
     * @param isVip 是否为VIP
     * @param expireTime VIP过期时间，null表示永久VIP
     * @return 设置后的用户信息
     * @throws IllegalArgumentException 如果用户不存在或设置失败
     */
    AdminUserDTO setVipStatus(Long userId, Boolean isVip, LocalDateTime expireTime);

    /**
     * 调整用户账户余额
     * @param userId 用户ID
     * @param amount 调整金额（正数为增加，负数为减少）
     * @param reason 调整原因
     * @return 调整后的用户信息
     * @throws IllegalArgumentException 如果用户不存在或余额不足
     */
    AdminUserDTO adjustBalance(Long userId, BigDecimal amount, String reason);

    /**
     * 获取用户统计信息
     * @return 用户统计信息，包含总用户数、VIP用户数、锁定用户数等
     */
    UserStatistics getUserStatistics();
} 