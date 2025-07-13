package com.example.video_interface.service.admin.impl;

import com.example.video_interface.dto.admin.AdminUserDTO;
import com.example.video_interface.dto.admin.AdminUserRequest;
import com.example.video_interface.dto.admin.UserStatistics;
import com.example.video_interface.model.User;
import com.example.video_interface.repository.UserRepository;
import com.example.video_interface.service.admin.IAdminUserService;
import com.example.video_interface.util.RequestContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员用户管理服务实现类
 * 提供用户查询、更新、删除等管理功能的具体实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserServiceImpl implements IAdminUserService {
    private final UserRepository userRepository;

    @Override
    public Page<AdminUserDTO> getUserList(AdminUserRequest request) {
        log.info("管理员查询用户列表: {}", request.getKeyword());
        
        // 构建分页和排序
        Sort sort = Sort.by(
            "desc".equalsIgnoreCase(request.getSortDirection()) ? 
            Sort.Direction.DESC : Sort.Direction.ASC, 
            request.getSortBy()
        );
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
        
        // 构建查询条件
        Specification<User> spec = buildUserSpecification(request);
        
        // 执行查询
        Page<User> userPage = userRepository.findAll(spec, pageable);
        
        // 转换为DTO
        return userPage.map(AdminUserDTO::fromUser);
    }

    @Override
    public AdminUserDTO getUserById(Long userId) {
        log.info("管理员查询用户详情: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        return AdminUserDTO.fromUser(user);
    }

    @Override
    public AdminUserDTO updateUser(AdminUserRequest request) {
        log.info("管理员更新用户信息: {}", request.getId());
        
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        // 更新用户信息
        if (StringUtils.hasText(request.getUsername())) {
            user.setUsername(request.getUsername());
        }
        if (StringUtils.hasText(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getPhoneNumber())) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (StringUtils.hasText(request.getAvatar())) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getIsVip() != null) {
            user.setIsVip(request.getIsVip());
        }
        if (request.getVipExpireTime() != null) {
            user.setVipExpireTime(request.getVipExpireTime());
        }
        if (request.getAccountBalance() != null) {
            user.setAccountBalance(request.getAccountBalance());
        }
        if (request.getWatchTime() != null) {
            user.setWatchTime(request.getWatchTime());
        }
        if (request.getIsLocked() != null) {
            user.setIsLocked(request.getIsLocked());
        }
        if (StringUtils.hasText(request.getLockReason())) {
            user.setLockReason(request.getLockReason());
        }
        if (request.getFailedLoginAttempts() != null) {
            user.setFailedLoginAttempts(request.getFailedLoginAttempts());
        }
        if (request.getLockTime() != null) {
            user.setLockTime(request.getLockTime());
        }
        if (request.getUnlockTime() != null) {
            user.setUnlockTime(request.getUnlockTime());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        
        User savedUser = userRepository.save(user);
        log.info("管理员更新用户信息成功: {}", savedUser.getUsername());
        
        return AdminUserDTO.fromUser(savedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("管理员删除用户: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        // 检查是否为管理员
        if (User.UserRole.ADMIN.equals(user.getRole())) {
            throw new IllegalArgumentException("不能删除管理员账户");
        }
        
        // 软删除：将状态设置为DELETED
        user.setStatus(User.UserStatus.DELETED);
        userRepository.save(user);
        
        log.info("管理员删除用户成功: {}", user.getUsername());
    }

    @Override
    public AdminUserDTO lockUser(Long userId, String reason, Integer lockDuration) {
        log.info("管理员锁定用户: {}, 原因: {}, 时长: {}分钟", userId, reason, lockDuration);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        // 检查是否为管理员
        if (User.UserRole.ADMIN.equals(user.getRole())) {
            throw new IllegalArgumentException("不能锁定管理员账户");
        }
        
        user.setIsLocked(true);
        user.setLockReason(reason);
        user.setLockTime(LocalDateTime.now());
        
        if (lockDuration != null && lockDuration > 0) {
            user.setUnlockTime(LocalDateTime.now().plusMinutes(lockDuration));
        } else {
            user.setUnlockTime(null); // 永久锁定
        }
        
        User savedUser = userRepository.save(user);
        log.info("管理员锁定用户成功: {}", savedUser.getUsername());
        
        return AdminUserDTO.fromUser(savedUser);
    }

    @Override
    public AdminUserDTO unlockUser(Long userId) {
        log.info("管理员解锁用户: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        user.setIsLocked(false);
        user.setLockReason(null);
        user.setLockTime(null);
        user.setUnlockTime(null);
        user.setFailedLoginAttempts(0);
        user.setLastFailedLoginTime(null);
        
        User savedUser = userRepository.save(user);
        log.info("管理员解锁用户成功: {}", savedUser.getUsername());
        
        return AdminUserDTO.fromUser(savedUser);
    }

    @Override
    public AdminUserDTO resetLoginAttempts(Long userId) {
        log.info("管理员重置用户登录失败次数: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        user.setFailedLoginAttempts(0);
        user.setLastFailedLoginTime(null);
        
        User savedUser = userRepository.save(user);
        log.info("管理员重置用户登录失败次数成功: {}", savedUser.getUsername());
        
        return AdminUserDTO.fromUser(savedUser);
    }

    @Override
    public AdminUserDTO setVipStatus(Long userId, Boolean isVip, LocalDateTime expireTime) {
        log.info("管理员设置用户VIP状态: {}, VIP: {}, 过期时间: {}", userId, isVip, expireTime);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        user.setIsVip(isVip);
        user.setVipExpireTime(expireTime);
        
        User savedUser = userRepository.save(user);
        log.info("管理员设置用户VIP状态成功: {}", savedUser.getUsername());
        
        return AdminUserDTO.fromUser(savedUser);
    }

    @Override
    public AdminUserDTO adjustBalance(Long userId, BigDecimal amount, String reason) {
        log.info("管理员调整用户余额: {}, 金额: {}, 原因: {}", userId, amount, reason);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        BigDecimal newBalance = user.getAccountBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("余额不足，无法完成调整");
        }
        
        user.setAccountBalance(newBalance);
        
        User savedUser = userRepository.save(user);
        log.info("管理员调整用户余额成功: {}, 新余额: {}", savedUser.getUsername(), newBalance);
        
        return AdminUserDTO.fromUser(savedUser);
    }

    @Override
    public UserStatistics getUserStatistics() {
        log.info("管理员获取用户统计信息");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.minusDays(7);
        LocalDateTime monthStart = now.minusDays(30);
        
        return UserStatistics.builder()
                .totalUsers(userRepository.countByStatusNotAndRoleNot(User.UserStatus.DELETED, User.UserRole.ADMIN))
                .vipUsers(userRepository.countByIsVipTrueAndStatusNotAndRoleNot(User.UserStatus.DELETED, User.UserRole.ADMIN))
                .lockedUsers(userRepository.countByIsLockedTrueAndStatusNotAndRoleNot(User.UserStatus.DELETED, User.UserRole.ADMIN))
                .todayNewUsers(userRepository.countByCreatedAtAfterAndStatusNotAndRoleNot(todayStart, User.UserStatus.DELETED, User.UserRole.ADMIN))
                .weekNewUsers(userRepository.countByCreatedAtAfterAndStatusNotAndRoleNot(weekStart, User.UserStatus.DELETED, User.UserRole.ADMIN))
                .monthNewUsers(userRepository.countByCreatedAtAfterAndStatusNotAndRoleNot(monthStart, User.UserStatus.DELETED, User.UserRole.ADMIN))
                .todayActiveUsers(userRepository.countByLastLoginTimeAfterAndStatusNotAndRoleNot(todayStart, User.UserStatus.DELETED, User.UserRole.ADMIN))
                .weekActiveUsers(userRepository.countByLastLoginTimeAfterAndStatusNotAndRoleNot(weekStart, User.UserStatus.DELETED, User.UserRole.ADMIN))
                .monthActiveUsers(userRepository.countByLastLoginTimeAfterAndStatusNotAndRoleNot(monthStart, User.UserStatus.DELETED, User.UserRole.ADMIN))
                .adminUsers(userRepository.countByRoleAndStatusNot(User.UserRole.ADMIN, User.UserStatus.DELETED))
                .normalUsers(userRepository.countByRoleAndStatusNot(User.UserRole.USER, User.UserStatus.DELETED))
                .build();
    }

    /**
     * 构建用户查询条件
     * @param request 查询请求
     * @return 查询条件
     */
    private Specification<User> buildUserSpecification(AdminUserRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 排除已删除用户
            predicates.add(criteriaBuilder.notEqual(root.get("status"), User.UserStatus.DELETED));
            
            // 排除管理员用户
            predicates.add(criteriaBuilder.notEqual(root.get("role"), User.UserRole.ADMIN));
            
            // 关键词搜索
            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = "%" + request.getKeyword() + "%";
                Predicate usernamePredicate = criteriaBuilder.like(root.get("username"), keyword);
                Predicate emailPredicate = criteriaBuilder.like(root.get("email"), keyword);
                Predicate phonePredicate = criteriaBuilder.like(root.get("phoneNumber"), keyword);
                predicates.add(criteriaBuilder.or(usernamePredicate, emailPredicate, phonePredicate));
            }
            
            // 状态筛选
            if (request.getStatusFilter() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatusFilter()));
            }
            
            // 角色筛选
            if (request.getRoleFilter() != null) {
                predicates.add(criteriaBuilder.equal(root.get("role"), request.getRoleFilter()));
            }
            
            // VIP状态筛选
            if (request.getVipFilter() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isVip"), request.getVipFilter()));
            }
            
            // 锁定状态筛选
            if (request.getLockedFilter() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isLocked"), request.getLockedFilter()));
            }
            
            // 时间范围筛选
            if (request.getStartTime() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), request.getStartTime()));
            }
            if (request.getEndTime() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), request.getEndTime()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
} 