package com.example.video_interface.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * H5登录失败次数管理服务
 * 用于跟踪H5端登录失败次数，基于IP地址存储在Redis中
 * 不会锁定账户，只用于判断是否显示验证码
 */
@Service
public class H5LoginFailureService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Redis key前缀
    private static final String H5_LOGIN_FAIL_PREFIX = "h5:login:fail:";
    
    // 失败次数超过此值需要验证码
    private static final int CAPTCHA_THRESHOLD = 3;
    
    // 失败记录过期时间（15分钟）
    private static final long EXPIRE_MINUTES = 15;

    /**
     * 获取Redis key
     * @param clientIp 客户端IP地址
     * @return Redis key
     */
    private String getRedisKey(String clientIp) {
        return H5_LOGIN_FAIL_PREFIX + clientIp;
    }

    /**
     * 增加登录失败次数
     * @param clientIp 客户端IP地址
     * @return 当前失败次数
     */
    public int incrementFailCount(String clientIp) {
        String key = getRedisKey(clientIp);
        
        // 获取当前失败次数
        Integer currentCount = (Integer) redisTemplate.opsForValue().get(key);
        if (currentCount == null) {
            currentCount = 0;
        }
        
        // 增加失败次数
        int newCount = currentCount + 1;
        redisTemplate.opsForValue().set(key, newCount, EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        return newCount;
    }

    /**
     * 获取当前登录失败次数
     * @param clientIp 客户端IP地址
     * @return 失败次数
     */
    public int getFailCount(String clientIp) {
        String key = getRedisKey(clientIp);
        Integer count = (Integer) redisTemplate.opsForValue().get(key);
        return count != null ? count : 0;
    }

    /**
     * 清除登录失败次数（登录成功时调用）
     * @param clientIp 客户端IP地址
     */
    public void clearFailCount(String clientIp) {
        String key = getRedisKey(clientIp);
        redisTemplate.delete(key);
    }

    /**
     * 判断是否需要验证码
     * @param clientIp 客户端IP地址
     * @return 是否需要验证码
     */
    public boolean needCaptcha(String clientIp) {
        return getFailCount(clientIp) >= CAPTCHA_THRESHOLD;
    }

    /**
     * 判断失败次数是否达到验证码阈值
     * @param failCount 失败次数
     * @return 是否需要验证码
     */
    public boolean needCaptcha(int failCount) {
        return failCount >= CAPTCHA_THRESHOLD;
    }
} 