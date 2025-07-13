package com.example.video_interface.service.admin.impl;

import com.example.video_interface.dto.admin.AdminLoginRequest;
import com.example.video_interface.dto.admin.AdminUserDTO;
import com.example.video_interface.dto.admin.AdminManagementRequest;
import com.example.video_interface.model.User;
import com.example.video_interface.repository.UserRepository;
import com.example.video_interface.security.JwtTokenProvider;
import com.example.video_interface.service.admin.IAdminService;
import com.example.video_interface.service.common.ILoginSecurityService;
import com.example.video_interface.util.RequestContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 管理员服务实现类
 * 实现管理员相关的业务逻辑，包括登录、登出、获取个人信息等操作
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements IAdminService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final ILoginSecurityService loginSecurityService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 管理员登录
     * @param request 登录请求，包含用户名和密码
     * @return 登录成功的管理员信息
     * @throws IllegalArgumentException 如果用户名或密码错误或不是管理员
     */
    @Override
    public User adminLogin(AdminLoginRequest request) {
        log.info(" 处理管理员登录请求: {}", request.getUsername());
        
        // 获取客户端IP地址
        String clientIp = RequestContextUtil.getClientIpAddress();
        log.debug("客户端IP: {}", clientIp);

        // 查找用户（先查找用户，即使密码错误也要记录失败次数）
        User admin = null;
        try {
            admin = userRepository.findByUsernameAndRole(request.getUsername(), User.UserRole.ADMIN)
                    .orElse(null);
        } catch (Exception e) {
            log.error("查找管理员用户失败: {}", e.getMessage());
        }

        // 🔍 检查账户锁定状态（如果用户存在）
        if (admin != null) {
            boolean lockResult = loginSecurityService.isAccountLocked(admin.getUsername());
            if (lockResult) {
                String message = "账户已暂时锁定";
                throw new IllegalArgumentException(message);
            }
        }

        try {
            // 🔐 验证用户名和密码
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 再次确认是管理员账户（防止数据变更）
            if (admin == null) {
                admin = userRepository.findByUsernameAndRole(request.getUsername(), User.UserRole.ADMIN)
                        .orElseThrow(() -> new IllegalArgumentException("该账号不是管理员账号"));
            }
            
            // 检查账号状态
            if (!admin.isEnabled()) {
                log.warn("管理员账号已被禁用: {}", request.getUsername());
                throw new IllegalArgumentException("管理员账号已被禁用");
            }

            // ✅ 登录成功，记录成功日志并重置失败次数
            loginSecurityService.resetLoginFailures(admin.getUsername());
            
            // 更新最后登录时间和IP
            admin.setLastLoginTime(LocalDateTime.now());
            admin.setLastLoginIp(clientIp);
            
            log.info("管理员登录成功: {} (IP: {})", admin.getUsername(), clientIp);
            return userRepository.save(admin);
            
        } catch (Exception e) {
            log.error("管理员登录失败: {} (IP: {}) - 原因: {}", 
                request.getUsername(), clientIp, e.getMessage());
            
            // 🔍 构造详细的错误信息
            String errorMessage = "管理员登录失败";
            
            try {
                // 📝 记录登录失败
                boolean accountLocked = loginSecurityService.recordLoginFailure(admin.getUsername());
                
                // 检查是否是账号被禁用的错误
                if (e.getMessage().contains("User is disabled") || e.getMessage().contains("管理员账号已被禁用")) {
                    errorMessage = "管理员账号已被禁用，请联系系统管理员";
                } else if (admin != null && e.getMessage().contains("Bad credentials")) {
                    // 重新查询用户以获取最新的失败次数
                    admin = userRepository.findByUsernameAndRole(request.getUsername(), User.UserRole.ADMIN)
                            .orElse(admin);
                    
                    int remainingAttempts = loginSecurityService.getRemainingAttempts(admin.getUsername());
                    if (remainingAttempts > 0 && !accountLocked) {
                        errorMessage = String.format("密码错误，您还有 %d 次尝试机会", remainingAttempts);
                    } else if (accountLocked) {
                        errorMessage = "登录失败次数过多，账户已被锁定";
                    } else {
                        errorMessage = "密码错误";
                    }
                } else {
                    // 其他错误
                    errorMessage = e.getMessage();
                }
            } catch (Exception recordException) {
                log.error("记录登录失败时出错: {}", recordException.getMessage());
                // 如果记录失败，使用通用错误消息
                if (e.getMessage().contains("User is disabled") || e.getMessage().contains("管理员账号已被禁用")) {
                    errorMessage = "管理员账号已被禁用，请联系系统管理员";
                } else if (admin != null && e.getMessage().contains("Bad credentials")) {
                    errorMessage = "密码错误";
                } else if (admin == null) {
                    errorMessage = "该账号不是管理员账号";
                } else {
                    errorMessage = e.getMessage();
                }
            }
            
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * 管理员登出
     * @param token JWT令牌
     */
    @Override
    public void adminLogout(String token) {
        try {
            // 将token加入Redis黑名单，设置过期时间与token剩余有效期一致
            String username = tokenProvider.getUsernameFromJWT(token);
            long expirationTime = tokenProvider.getExpirationDateFromToken(token).getTime();
            long currentTime = System.currentTimeMillis();
            long remainingTime = expirationTime - currentTime;
            
            if (remainingTime > 0) {
                String key = "blacklist:" + token;
                stringRedisTemplate.opsForValue().set(key, username, remainingTime, TimeUnit.MILLISECONDS);
                log.debug("Token已加入黑名单，管理员: {}", username);
            }
        } catch (Exception e) {
            log.error("管理员登出过程发生错误: {}", e.getMessage());
            throw new RuntimeException("退出登录失败");
        }
    }

    /**
     * 获取当前管理员信息
     * @return 当前管理员信息
     */
    @Override
    public User getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("管理员未登录");
        }

        User admin = userRepository.findByUsernameAndRole(authentication.getName(), User.UserRole.ADMIN)
                .orElseThrow(() -> new IllegalArgumentException("管理员账号不存在"));

        if (!admin.isEnabled()) {
            throw new IllegalStateException("管理员账号已被禁用");
        }

        return admin;
    }

    /**
     * 获取管理员列表
     * @param request 查询请求参数
     * @return 管理员列表分页数据
     */
    @Override
    public Map<String, Object> getAdminList(AdminManagementRequest request) {
        log.info("获取管理员列表，页码: {}, 大小: {}", request.getPage(), request.getSize());
        
        try {
            // 构建分页参数
            Pageable pageable = PageRequest.of(
                request.getPage() - 1, 
                request.getSize(), 
                Sort.by(Sort.Direction.DESC, "createdAt")
            );

            // 构建查询条件
            String username = StringUtils.hasText(request.getUsername()) ? request.getUsername() : null;
            String email = StringUtils.hasText(request.getEmail()) ? request.getEmail() : null;
            Boolean enabled = request.getEnabled();

            // 查询管理员列表
            Page<User> adminPage = userRepository.findAdminsWithFilters(
                username, email, enabled, 
                User.UserRole.ADMIN, 
                User.UserStatus.ACTIVE, 
                User.UserStatus.INACTIVE, 
                pageable
            );
            
            // 转换为DTO
            List<AdminUserDTO> adminList = adminPage.getContent().stream()
                .map(this::convertToAdminUserDTO)
                .collect(Collectors.toList());

            // 统计信息
            long totalAdmins = userRepository.countByRole(User.UserRole.ADMIN);
            long enabledAdmins = userRepository.countByRoleAndStatusNot(User.UserRole.ADMIN, User.UserStatus.INACTIVE);
            long disabledAdmins = userRepository.countByRoleAndStatusNot(User.UserRole.ADMIN, User.UserStatus.ACTIVE);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "获取管理员列表成功");
            result.put("data", Map.of(
                "list", adminList,
                "total", adminPage.getTotalElements(),
                "page", request.getPage(),
                "size", request.getSize(),
                "totalPages", adminPage.getTotalPages(),
                "statistics", Map.of(
                    "totalAdmins", totalAdmins,
                    "enabledAdmins", enabledAdmins,
                    "disabledAdmins", disabledAdmins
                )
            ));

            log.info("获取管理员列表成功，总数: {}", adminPage.getTotalElements());
            return result;

        } catch (Exception e) {
            log.error("获取管理员列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "获取管理员列表失败: " + e.getMessage());
            result.put("data", null);
            return result;
        }
    }

    /**
     * 创建管理员
     * @param request 创建请求参数
     * @return 创建结果
     */
    @Override
    public Map<String, Object> createAdmin(AdminManagementRequest request) {
        log.info("创建管理员: {}", request.getUsername());
        
        try {
            // 检查用户名是否已存在
            if (userRepository.existsByUsername(request.getUsername())) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 400);
                result.put("message", "用户名已存在");
                result.put("data", null);
                return result;
            }

            // 检查邮箱是否已存在
            if (StringUtils.hasText(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 400);
                result.put("message", "邮箱已存在");
                result.put("data", null);
                return result;
            }

            // 创建管理员用户
            User admin = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(User.UserRole.ADMIN)
                .status(User.UserStatus.ACTIVE)
                .build();

            User savedAdmin = userRepository.save(admin);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "创建管理员成功");
            result.put("data", convertToAdminUserDTO(savedAdmin));

            log.info("创建管理员成功: {}", savedAdmin.getUsername());
            return result;

        } catch (Exception e) {
            log.error("创建管理员失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "创建管理员失败: " + e.getMessage());
            result.put("data", null);
            return result;
        }
    }

    /**
     * 更新管理员
     * @param request 更新请求参数
     * @return 更新结果
     */
    @Override
    public Map<String, Object> updateAdmin(AdminManagementRequest request) {
        log.info("更新管理员: {}", request.getId());
        
        try {
            User admin = userRepository.findById(request.getId())
                .orElse(null);

            if (admin == null || admin.getRole() != User.UserRole.ADMIN) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 404);
                result.put("message", "管理员不存在");
                result.put("data", null);
                return result;
            }

            // 检查用户名是否被其他用户使用
            if (StringUtils.hasText(request.getUsername()) && 
                !request.getUsername().equals(admin.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 400);
                result.put("message", "用户名已存在");
                result.put("data", null);
                return result;
            }

            // 检查邮箱是否被其他用户使用
            if (StringUtils.hasText(request.getEmail()) && 
                !request.getEmail().equals(admin.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 400);
                result.put("message", "邮箱已存在");
                result.put("data", null);
                return result;
            }

            // 更新字段
            if (StringUtils.hasText(request.getUsername())) {
                admin.setUsername(request.getUsername());
            }
            if (StringUtils.hasText(request.getPassword())) {
                admin.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            if (StringUtils.hasText(request.getEmail())) {
                admin.setEmail(request.getEmail());
            }
            User updatedAdmin = userRepository.save(admin);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "更新管理员成功");
            result.put("data", convertToAdminUserDTO(updatedAdmin));

            log.info("更新管理员成功: {}", updatedAdmin.getUsername());
            return result;

        } catch (Exception e) {
            log.error("更新管理员失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "更新管理员失败: " + e.getMessage());
            result.put("data", null);
            return result;
        }
    }

    /**
     * 删除管理员
     * @param request 删除请求参数
     * @return 删除结果
     */
    @Override
    public Map<String, Object> deleteAdmin(AdminManagementRequest request) {
        log.info("删除管理员: {}", request.getId());
        
        try {
            User admin = userRepository.findById(request.getId())
                .orElse(null);

            if (admin == null || admin.getRole() != User.UserRole.ADMIN) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 404);
                result.put("message", "管理员不存在");
                result.put("data", null);
                return result;
            }

            // 检查是否为当前登录的管理员
            User currentAdmin = getCurrentAdmin();
            if (admin.getId().equals(currentAdmin.getId())) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 400);
                result.put("message", "不能删除当前登录的管理员");
                result.put("data", null);
                return result;
            }

            userRepository.delete(admin);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "删除管理员成功");
            result.put("data", null);

            log.info("删除管理员成功: {}", admin.getUsername());
            return result;

        } catch (Exception e) {
            log.error("删除管理员失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "删除管理员失败: " + e.getMessage());
            result.put("data", null);
            return result;
        }
    }

    /**
     * 启用/禁用管理员
     * @param request 状态更新请求参数
     * @return 更新结果
     */
    @Override
    public Map<String, Object> toggleAdminStatus(AdminManagementRequest request) {
        log.info("切换管理员状态: {}, 启用状态: {}", request.getId(), request.getEnabled());
        
        try {
            User admin = userRepository.findById(request.getId())
                .orElse(null);

            if (admin == null || admin.getRole() != User.UserRole.ADMIN) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 404);
                result.put("message", "管理员不存在");
                result.put("data", null);
                return result;
            }

            // 检查是否为当前登录的管理员
            User currentAdmin = getCurrentAdmin();
            if (admin.getId().equals(currentAdmin.getId())) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 400);
                result.put("message", "不能修改当前登录管理员的状态");
                result.put("data", null);
                return result;
            }

            admin.setStatus(request.getEnabled() ? User.UserStatus.ACTIVE : User.UserStatus.INACTIVE);
            User updatedAdmin = userRepository.save(admin);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", request.getEnabled() ? "启用管理员成功" : "禁用管理员成功");
            result.put("data", convertToAdminUserDTO(updatedAdmin));

            log.info("切换管理员状态成功: {} -> {}", admin.getUsername(), request.getEnabled());
            return result;

        } catch (Exception e) {
            log.error("切换管理员状态失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "切换管理员状态失败: " + e.getMessage());
            result.put("data", null);
            return result;
        }
    }

    /**
     * 转换为AdminUserDTO
     * @param user 用户对象
     * @return AdminUserDTO对象
     */
    private AdminUserDTO convertToAdminUserDTO(User user) {
        return AdminUserDTO.fromUser(user);
    }
} 