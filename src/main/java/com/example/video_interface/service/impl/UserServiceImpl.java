package com.example.video_interface.service.impl;

import com.example.video_interface.dto.LoginRequest;
import com.example.video_interface.dto.RegisterRequest;
import com.example.video_interface.model.User;
import com.example.video_interface.repository.UserRepository;
import com.example.video_interface.security.JwtTokenProvider;
import com.example.video_interface.service.LoginSecurityService;
import com.example.video_interface.service.UserService;
import com.example.video_interface.util.RequestContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 * 实现用户相关的业务逻辑，包括用户注册、登录、信息管理等操作
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final LoginSecurityService loginSecurityService;

    /**
     * 用户注册
     * @param request 注册请求，包含用户名和密码
     * @return 注册成功的用户信息
     * @throws IllegalArgumentException 如果用户名已存在或参数无效
     */
    @Override
    @Transactional
    public User registerUser(RegisterRequest request) {
        log.info("开始处理用户注册请求: {}", request.getUsername());

        // 验证请求参数
        validateRegisterRequest(request);

        try {
            // 创建新用户
            User user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .status(User.UserStatus.ACTIVE)
                    .role(User.UserRole.USER)
                    .build();

            log.info("保存新用户到数据库");
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("用户注册失败: {}", e.getMessage(), e);
            throw new RuntimeException("注册失败，请稍后重试");
        }
    }

    /**
     * 验证注册请求参数
     * @param request 注册请求
     * @throws IllegalArgumentException 如果参数无效
     */
    private void validateRegisterRequest(RegisterRequest request) {
        // 验证用户名
        if (!StringUtils.hasText(request.getUsername())) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (request.getUsername().length() < 3 || request.getUsername().length() > 20) {
            throw new IllegalArgumentException("用户名长度必须在3-20个字符之间");
        }
        if (!request.getUsername().matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("用户名只能包含字母、数字和下划线");
        }

        // 验证密码
        if (!StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (request.getPassword().length() < 6 || request.getPassword().length() > 20) {
            throw new IllegalArgumentException("密码长度必须在6-20个字符之间");
        }

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("用户名已存在: {}", request.getUsername());
            throw new IllegalArgumentException("用户名已被使用");
        }
    }

    /**
     * 用户登录
     * @param request 登录请求，包含用户名和密码
     * @return 登录成功的用户信息
     * @throws IllegalArgumentException 如果用户名或密码错误
     */
    @Override
    public User loginUser(LoginRequest request) {
        log.info("处理用户登录请求: {}", request.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
            
            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("用户登录失败: {}", request.getUsername(), e);
            throw new IllegalArgumentException("用户名或密码错误");
        }
    }

    /**
     * 为用户生成JWT令牌
     * @param user 用户信息
     * @return JWT令牌
     */
    @Override
    public String generateToken(User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user,  // 传递User对象（实现了UserDetails）而不是用户名字符串
            null,
            user.getAuthorities()
        );
        return tokenProvider.generateToken(authentication);
    }

    /**
     * 用户登出
     * @param token 用户令牌
     */
    @Override
    public void logout(String token) {
        try {
            // 将token加入Redis黑名单，设置过期时间与token剩余有效期一致
            String username = tokenProvider.getUsernameFromJWT(token);
            long expirationTime = tokenProvider.getExpirationDateFromToken(token).getTime();
            long currentTime = System.currentTimeMillis();
            long remainingTime = expirationTime - currentTime;
            
            if (remainingTime > 0) {
                String key = "blacklist:" + token;
                redisTemplate.opsForValue().set(key, username, remainingTime, TimeUnit.MILLISECONDS);
                log.debug("Token已加入黑名单，用户: {}", username);
            }
        } catch (Exception e) {
            log.error("登出过程发生错误: {}", e.getMessage());
            throw new RuntimeException("退出登录失败");
        }
    }

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息，如果不存在则返回空
     */
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return true如果用户名已存在，否则返回false
     */
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 检查邮箱是否已存在
     * @param email 邮箱地址
     * @return true如果邮箱已存在，否则返回false
     */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 更新用户密码
     * @param username 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @throws IllegalArgumentException 如果旧密码错误
     */
    @Override
    public void updatePassword(String username, String oldPassword, String newPassword) {
        User currentUser = findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }

        // 更新密码
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
    }

    /**
     * 更新用户基本信息
     * @param username 用户名
     * @param avatar 头像URL
     * @param phoneNumber 手机号码
     * @return 更新后的用户信息
     */
    @Override
    public User updateProfile(String username, String avatar, String phoneNumber) {
        User user = findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        if (avatar != null) {
            user.setAvatar(avatar);
        }
        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber);
        }

        return userRepository.save(user);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("用户未登录");
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        User existingUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 更新用户信息
        if (StringUtils.hasText(user.getAvatar())) {
            existingUser.setAvatar(user.getAvatar());
        }
        if (StringUtils.hasText(user.getPhoneNumber())) {
            existingUser.setPhoneNumber(user.getPhoneNumber());
        }
        if (StringUtils.hasText(user.getEmail())) {
            existingUser.setEmail(user.getEmail());
        }

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public User updateCurrentUser(Map<String, String> userData) {
        User currentUser = getCurrentUser();

        // 更新用户信息
        if (userData.containsKey("avatar")) {
            currentUser.setAvatar(userData.get("avatar"));
        }
        if (userData.containsKey("phoneNumber")) {
            currentUser.setPhoneNumber(userData.get("phoneNumber"));
        }
        if (userData.containsKey("email")) {
            String newEmail = userData.get("email");
            if (!newEmail.equals(currentUser.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("邮箱已被使用");
            }
            currentUser.setEmail(newEmail);
        }

        return userRepository.save(currentUser);
    }
    
    /**
     * 管理员登录
     * @param request 登录请求，包含用户名和密码
     * @return 登录成功的管理员信息
     * @throws IllegalArgumentException 如果用户名或密码错误或不是管理员
     */
    @Override
    public User adminLogin(LoginRequest request) {
        log.info("🔐 处理管理员登录请求: {}", request.getUsername());
        
        // 获取客户端IP地址
        String clientIp = RequestContextUtil.getClientIpAddress();
        log.debug("📍 客户端IP: {}", clientIp);

        // 🛡️ 检查IP是否被锁定
        if (loginSecurityService.isIpLocked(clientIp)) {
            log.warn("🚫 IP已被锁定，拒绝登录: {} (IP: {})", request.getUsername(), clientIp);
            throw new IllegalArgumentException("IP地址已被锁定，请稍后再试");
        }

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
            LoginSecurityService.LockCheckResult lockResult = loginSecurityService.checkAccountLock(admin);
            if (lockResult.isLocked()) {
                String message = lockResult.getReason();
                if (lockResult.getUnlockTime() != null) {
                    message += "，预计解锁时间: " + lockResult.getUnlockTime();
                }
                log.warn("🔒 账户已锁定: {} - {}", request.getUsername(), message);
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
                log.warn("❌ 管理员账号已被禁用: {}", request.getUsername());
                throw new IllegalArgumentException("管理员账号已被禁用");
            }

            // ✅ 登录成功，记录成功日志并重置失败次数
            loginSecurityService.recordLoginSuccess(admin, clientIp);
            
            // 更新最后登录时间和IP
            admin.setLastLoginTime(LocalDateTime.now());
            admin.setLastLoginIp(clientIp);
            
            log.info("✅ 管理员登录成功: {} (IP: {})", admin.getUsername(), clientIp);
            return userRepository.save(admin);
            
        } catch (Exception e) {
            log.error("🚨 管理员登录失败: {} (IP: {}) - 原因: {}", 
                request.getUsername(), clientIp, e.getMessage());
            
            // 🔍 构造详细的错误信息
            String errorMessage = "管理员登录失败";
            
            try {
                // 📝 记录登录失败（即使用户不存在也要记录IP失败）
                boolean accountLocked = loginSecurityService.recordLoginFailure(admin, clientIp);
                
                // 如果是密码错误且用户存在，提供剩余尝试次数信息
                if (admin != null && e.getMessage().contains("Bad credentials")) {
                    // 重新查询用户以获取最新的失败次数
                    admin = userRepository.findByUsernameAndRole(request.getUsername(), User.UserRole.ADMIN)
                            .orElse(admin);
                    
                    int remainingAttempts = loginSecurityService.getRemainingAttempts(admin);
                    if (remainingAttempts > 0 && !accountLocked) {
                        errorMessage = String.format("密码错误，您还有 %d 次尝试机会", remainingAttempts);
                    } else if (accountLocked) {
                        errorMessage = "登录失败次数过多，账户已被锁定";
                    } else {
                        errorMessage = "密码错误";
                    }
                } else if (admin == null) {
                    // 用户不存在
                    errorMessage = "该账号不是管理员账号";
                } else {
                    // 其他错误
                    errorMessage = e.getMessage();
                }
            } catch (Exception recordException) {
                log.error("记录登录失败时出错: {}", recordException.getMessage());
                // 如果记录失败，使用通用错误消息
                if (admin != null && e.getMessage().contains("Bad credentials")) {
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
     * 创建初始管理员账号
     * @param username 管理员用户名
     * @param password 管理员密码
     * @param email 管理员邮箱
     * @return 创建成功的管理员信息
     * @throws IllegalArgumentException 如果参数无效或已存在管理员
     */
    @Override
    @Transactional
    public User createInitialAdmin(String username, String password, String email) {
        log.info("开始创建初始管理员账号: {}", username);

        // 检查是否已存在管理员
        if (hasAdminUser()) {
            throw new IllegalArgumentException("系统已存在管理员账号，无法重复创建");
        }

        // 验证参数
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("管理员用户名不能为空");
        }
        if (username.length() < 3 || username.length() > 20) {
            throw new IllegalArgumentException("管理员用户名长度必须在3-20个字符之间");
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("管理员用户名只能包含字母、数字和下划线");
        }

        if (!StringUtils.hasText(password)) {
            throw new IllegalArgumentException("管理员密码不能为空");
        }
        if (password.length() < 6 || password.length() > 20) {
            throw new IllegalArgumentException("管理员密码长度必须在6-20个字符之间");
        }

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("管理员用户名已被使用");
        }

        // 检查邮箱是否已存在（如果提供了邮箱）
        if (StringUtils.hasText(email) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("邮箱地址已被使用");
        }

        try {
            // 创建管理员用户
            User admin = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .status(User.UserStatus.ACTIVE)
                    .role(User.UserRole.ADMIN)
                    .build();

            log.info("保存初始管理员到数据库");
            User savedAdmin = userRepository.save(admin);
            log.info("初始管理员创建成功: {}", savedAdmin.getUsername());
            return savedAdmin;
        } catch (Exception e) {
            log.error("创建初始管理员失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建初始管理员失败，请稍后重试");
        }
    }

    /**
     * 检查系统是否已有管理员
     * @return true如果已存在管理员，否则返回false
     */
    @Override
    public boolean hasAdminUser() {
        return userRepository.existsByRole(User.UserRole.ADMIN);
    }

    /**
     * 获取管理员数量
     * @return 管理员账号数量
     */
    @Override
    public long getAdminCount() {
        return userRepository.countByRole(User.UserRole.ADMIN);
    }
} 