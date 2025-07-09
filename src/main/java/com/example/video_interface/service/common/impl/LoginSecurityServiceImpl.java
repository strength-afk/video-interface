package com.example.video_interface.service.common.impl;

import com.example.video_interface.service.common.ILoginSecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 登录安全服务实现类
 * 实现管理员账户锁定相关功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginSecurityServiceImpl implements ILoginSecurityService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String LOGIN_FAILURE_PREFIX = "login:failure:";
    private static final int MAX_FAILURES = 5;
    private static final int LOCK_DURATION_MINUTES = 30;

    /**
     * 记录登录失败
     * @param username 用户名
     */
    @Override
    public boolean recordLoginFailure(String username) {
        String key = LOGIN_FAILURE_PREFIX + username;
        String failuresStr = redisTemplate.opsForValue().get(key);
        int failures = failuresStr != null ? Integer.parseInt(failuresStr) : 0;
        failures++;

        // 更新失败次数
        redisTemplate.opsForValue().set(key, String.valueOf(failures), LOCK_DURATION_MINUTES, TimeUnit.MINUTES);
        log.debug("记录登录失败 - 用户: {}, 失败次数: {}", username, failures);

        // 如果达到最大失败次数，锁定账户
        if (failures >= MAX_FAILURES) {
            log.warn("账户已锁定 - 用户: {}, 失败次数: {}", username, failures);
        }
        return failures >= MAX_FAILURES;
    }

    /**
     * 检查账户是否被锁定
     * @param username 用户名
     * @return 是否被锁定
     */
    @Override
    public boolean isAccountLocked(String username) {
        String key = LOGIN_FAILURE_PREFIX + username;
        String failuresStr = redisTemplate.opsForValue().get(key);
        
        if (failuresStr != null) {
            int failures = Integer.parseInt(failuresStr);
            boolean locked = failures >= MAX_FAILURES;
            
            if (locked) {
                log.debug("账户锁定检查 - 用户: {}, 已锁定", username);
            }
            
            return locked;
        }
        
        return false;
    }

    /**
     * 重置登录失败次数
     * @param username 用户名
     */
    @Override
    public void resetLoginFailures(String username) {
        String key = LOGIN_FAILURE_PREFIX + username;
        redisTemplate.delete(key);
        log.debug("重置登录失败次数 - 用户: {}", username);
    }

    /**
     * 获取剩余尝试次数
     * @param username 用户名
     */
    @Override
    public int getRemainingAttempts(String username) {
        String key = LOGIN_FAILURE_PREFIX + username;
        String failuresStr = redisTemplate.opsForValue().get(key);
        int failures = failuresStr != null ? Integer.parseInt(failuresStr) : 0;
        return MAX_FAILURES - failures;
    }
} 