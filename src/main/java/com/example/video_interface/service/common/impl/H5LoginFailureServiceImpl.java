package com.example.video_interface.service.common.impl;

import com.example.video_interface.service.common.IH5LoginFailureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * H5登录失败服务实现类
 * 实现H5端登录失败次数管理功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class H5LoginFailureServiceImpl implements IH5LoginFailureService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String LOGIN_FAILURE_PREFIX = "h5:login:failure:";
    private static final int CAPTCHA_THRESHOLD = 3;
    private static final int EXPIRE_MINUTES = 15;

    /**
     * 记录登录失败
     * @param ip 用户IP地址
     * @return 是否需要验证码
     */
    @Override
    public boolean recordLoginFailure(String ip) {
        String key = LOGIN_FAILURE_PREFIX + ip;
        String failuresStr = redisTemplate.opsForValue().get(key);
        int failures = failuresStr != null ? Integer.parseInt(failuresStr) : 0;
        failures++;

        // 更新失败次数
        redisTemplate.opsForValue().set(key, String.valueOf(failures), EXPIRE_MINUTES, TimeUnit.MINUTES);
        log.debug("记录H5登录失败 - IP: {}, 失败次数: {}", ip, failures);

        return failures >= CAPTCHA_THRESHOLD;
    }

    /**
     * 检查是否需要验证码
     * @param ip 用户IP地址
     * @return 是否需要验证码
     */
    @Override
    public boolean needCaptcha(String ip) {
        String key = LOGIN_FAILURE_PREFIX + ip;
        String failuresStr = redisTemplate.opsForValue().get(key);
        
        if (failuresStr != null) {
            int failures = Integer.parseInt(failuresStr);
            boolean needCaptcha = failures >= CAPTCHA_THRESHOLD;
            
            if (needCaptcha) {
                log.debug("需要验证码 - IP: {}, 失败次数: {}", ip, failures);
            }
            
            return needCaptcha;
        }
        
        return false;
    }

    /**
     * 重置登录失败次数
     * @param ip 用户IP地址
     */
    @Override
    public void resetLoginFailures(String ip) {
        String key = LOGIN_FAILURE_PREFIX + ip;
        redisTemplate.delete(key);
        log.debug("重置H5登录失败次数 - IP: {}", ip);
    }
} 