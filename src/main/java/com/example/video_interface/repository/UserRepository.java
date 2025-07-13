package com.example.video_interface.repository;

import com.example.video_interface.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    /**
     * 查找管理员角色的用户
     * @param role 用户角色
     * @return 管理员用户数量
     */
    long countByRole(User.UserRole role);
    
    /**
     * 检查是否存在管理员角色的用户
     * @param role 用户角色
     * @return true如果存在管理员，否则返回false
     */
    boolean existsByRole(User.UserRole role);
    
    /**
     * 根据用户名和角色查找用户
     * @param username 用户名
     * @param role 用户角色
     * @return 用户信息，如果不存在则返回空
     */
    Optional<User> findByUsernameAndRole(String username, User.UserRole role);
    
    /**
     * 统计非指定状态的用户数量
     * @param status 用户状态
     * @return 用户数量
     */
    long countByStatusNot(User.UserStatus status);
    
    /**
     * 统计VIP用户且非指定状态的数量
     * @param status 用户状态
     * @return VIP用户数量
     */
    long countByIsVipTrueAndStatusNot(User.UserStatus status);
    
    /**
     * 统计锁定用户且非指定状态的数量
     * @param status 用户状态
     * @return 锁定用户数量
     */
    long countByIsLockedTrueAndStatusNot(User.UserStatus status);
    
    /**
     * 统计指定时间后创建且非指定状态的用户数量
     * @param createdAt 创建时间
     * @param status 用户状态
     * @return 用户数量
     */
    long countByCreatedAtAfterAndStatusNot(LocalDateTime createdAt, User.UserStatus status);
    
    /**
     * 统计指定时间后登录且非指定状态的用户数量
     * @param lastLoginTime 最后登录时间
     * @param status 用户状态
     * @return 用户数量
     */
    long countByLastLoginTimeAfterAndStatusNot(LocalDateTime lastLoginTime, User.UserStatus status);
    
    /**
     * 统计指定角色且非指定状态的用户数量
     * @param role 用户角色
     * @param status 用户状态
     * @return 用户数量
     */
    long countByRoleAndStatusNot(User.UserRole role, User.UserStatus status);
    
    /**
     * 统计非指定状态且非指定角色的用户数量
     * @param status 用户状态
     * @param role 用户角色
     * @return 用户数量
     */
    long countByStatusNotAndRoleNot(User.UserStatus status, User.UserRole role);
    
    /**
     * 统计VIP用户且非指定状态且非指定角色的数量
     * @param status 用户状态
     * @param role 用户角色
     * @return VIP用户数量
     */
    long countByIsVipTrueAndStatusNotAndRoleNot(User.UserStatus status, User.UserRole role);
    
    /**
     * 统计锁定用户且非指定状态且非指定角色的数量
     * @param status 用户状态
     * @param role 用户角色
     * @return 锁定用户数量
     */
    long countByIsLockedTrueAndStatusNotAndRoleNot(User.UserStatus status, User.UserRole role);
    
    /**
     * 统计指定时间后创建且非指定状态且非指定角色的用户数量
     * @param createdAt 创建时间
     * @param status 用户状态
     * @param role 用户角色
     * @return 用户数量
     */
    long countByCreatedAtAfterAndStatusNotAndRoleNot(LocalDateTime createdAt, User.UserStatus status, User.UserRole role);
    
    /**
     * 统计指定时间后登录且非指定状态且非指定角色的用户数量
     * @param lastLoginTime 最后登录时间
     * @param status 用户状态
     * @param role 用户角色
     * @return 用户数量
     */
    long countByLastLoginTimeAfterAndStatusNotAndRoleNot(LocalDateTime lastLoginTime, User.UserStatus status, User.UserRole role);
    
    /**
     * 根据ID和角色查找用户
     * @param id 用户ID
     * @param role 用户角色
     * @return 用户信息
     */
    Optional<User> findByIdAndRole(Long id, User.UserRole role);
    
    /**
     * 统计指定角色和锁定状态的用户数量
     * @param role 用户角色
     * @param isLocked 是否锁定
     * @return 用户数量
     */
    long countByRoleAndIsLocked(User.UserRole role, boolean isLocked);
    
    /**
     * 统计指定角色且在指定时间后创建的用户数量
     * @param role 用户角色
     * @param createdAt 创建时间
     * @return 用户数量
     */
    long countByRoleAndCreatedAtAfter(User.UserRole role, LocalDateTime createdAt);
    
    /**
     * 根据条件查询管理员列表
     * @param username 用户名（可选）
     * @param email 邮箱（可选）
     * @param enabled 是否启用（可选）
     * @param pageable 分页参数
     * @return 管理员分页数据
     */
    @Query("SELECT u FROM User u WHERE u.role = :adminRole " +
           "AND (:username IS NULL OR u.username LIKE %:username%) " +
           "AND (:email IS NULL OR u.email LIKE %:email%) " +
           "AND (:enabled IS NULL OR u.status = CASE WHEN :enabled = true THEN :activeStatus ELSE :inactiveStatus END)")
    org.springframework.data.domain.Page<User> findAdminsWithFilters(
        String username, String email, Boolean enabled, 
        @org.springframework.data.repository.query.Param("adminRole") User.UserRole adminRole,
        @org.springframework.data.repository.query.Param("activeStatus") User.UserStatus activeStatus,
        @org.springframework.data.repository.query.Param("inactiveStatus") User.UserStatus inactiveStatus,
        org.springframework.data.domain.Pageable pageable);
    
    /**
     * 查找所有已锁定且解锁时间已到的用户
     * @param unlockTime 解锁时间
     * @return 需要自动解锁的用户列表
     */
    List<User> findByIsLockedTrueAndUnlockTimeBefore(LocalDateTime unlockTime);
    
    /**
     * 查找所有已锁定的用户
     * @return 已锁定的用户列表
     */
    List<User> findByIsLockedTrue();
} 