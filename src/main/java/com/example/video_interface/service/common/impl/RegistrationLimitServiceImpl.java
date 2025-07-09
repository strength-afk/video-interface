package com.example.video_interface.service.common.impl;

import com.example.video_interface.service.common.IRegistrationLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 注册限制服务实现类
 * 使用Redis存储IP地址的注册次数，防止同一IP短时间内大量注册
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationLimitServiceImpl implements IRegistrationLimitService {
    private final RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 注册限制阈值：每小时最多注册3个账号
     */
    private static final int MAX_REGISTRATIONS_PER_HOUR = 3;
    
    /**
     * 限制时间窗口：1小时
     */
    private static final long LIMIT_TIME_WINDOW_HOURS = 1;
    
    /**
     * Redis键前缀
     */
    private static final String REDIS_KEY_PREFIX = "registration:limit:";
    
    /**
     * 检查IP是否可以注册
     * @param clientIp 客户端IP地址
     * @return true如果可以注册，false如果已达到限制
     */
    @Override
    public boolean canRegister(String clientIp) {
        String key = REDIS_KEY_PREFIX + clientIp;
        
        try {
            Object countObj = redisTemplate.opsForValue().get(key);
            int currentCount = 0;
            
            if (countObj != null) {
                if (countObj instanceof String) {
                    currentCount = Integer.parseInt((String) countObj);
                } else if (countObj instanceof Integer) {
                    currentCount = (Integer) countObj;
                } else if (countObj instanceof Long) {
                    currentCount = ((Long) countObj).intValue();
                }
            }
            
            boolean canRegister = currentCount < MAX_REGISTRATIONS_PER_HOUR;
            
            log.debug("IP {} 当前注册次数: {}, 限制: {}, 可注册: {}", 
                clientIp, currentCount, MAX_REGISTRATIONS_PER_HOUR, canRegister);
            
            return canRegister;
        } catch (Exception e) {
            log.error("检查注册限制失败，IP: {}, 错误: {}", clientIp, e.getMessage());
            // 发生错误时允许注册，避免影响正常用户
            return true;
        }
    }
    
    /**
     * 获取当前注册次数
     * @param clientIp 客户端IP地址
     * @return 当前注册次数
     */
    @Override
    public int getCurrentCount(String clientIp) {
        String key = REDIS_KEY_PREFIX + clientIp;
        
        try {
            Object countObj = redisTemplate.opsForValue().get(key);
            int currentCount = 0;
            
            if (countObj != null) {
                if (countObj instanceof String) {
                    currentCount = Integer.parseInt((String) countObj);
                } else if (countObj instanceof Integer) {
                    currentCount = (Integer) countObj;
                } else if (countObj instanceof Long) {
                    currentCount = ((Long) countObj).intValue();
                }
            }
            
            return currentCount;
        } catch (Exception e) {
            log.error("获取注册次数失败，IP: {}, 错误: {}", clientIp, e.getMessage());
            return 0;
        }
    }
    
    /**
     * 记录一次注册
     * @param clientIp 客户端IP地址
     * @return 更新后的注册次数
     */
    @Override
    public int recordRegistration(String clientIp) {
        String key = REDIS_KEY_PREFIX + clientIp;
        
        try {
            // 增加计数
            Long newCount = redisTemplate.opsForValue().increment(key);
            
            // 设置过期时间（首次创建时）
            if (newCount == 1) {
                redisTemplate.expire(key, LIMIT_TIME_WINDOW_HOURS, TimeUnit.HOURS);
                log.debug("为IP {} 设置注册限制窗口: {} 小时", clientIp, LIMIT_TIME_WINDOW_HOURS);
            }
            
            int count = newCount.intValue();
            log.info("记录注册，IP: {}, 当前次数: {}/{}", clientIp, count, MAX_REGISTRATIONS_PER_HOUR);
            
            return count;
        } catch (Exception e) {
            log.error("记录注册失败，IP: {}, 错误: {}", clientIp, e.getMessage());
            return 0;
        }
    }
    
    /**
     * 获取剩余可注册次数
     * @param clientIp 客户端IP地址
     * @return 剩余可注册次数
     */
    @Override
    public int getRemainingCount(String clientIp) {
        int currentCount = getCurrentCount(clientIp);
        return Math.max(0, MAX_REGISTRATIONS_PER_HOUR - currentCount);
    }
    
    /**
     * 获取限制重置时间（秒）
     * @param clientIp 客户端IP地址
     * @return 限制重置时间（秒），如果没有限制返回0
     */
    @Override
    public long getResetTimeInSeconds(String clientIp) {
        String key = REDIS_KEY_PREFIX + clientIp;
        
        try {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return ttl != null && ttl > 0 ? ttl : 0;
        } catch (Exception e) {
            log.error("获取限制重置时间失败，IP: {}, 错误: {}", clientIp, e.getMessage());
            return 0;
        }
    }
    
    /**
     * 手动清除IP的注册限制（仅用于管理员操作）
     * @param clientIp 客户端IP地址
     */
    @Override
    public void clearRegistrationLimit(String clientIp) {
        String key = REDIS_KEY_PREFIX + clientIp;
        
        try {
            redisTemplate.delete(key);
            log.info("已清除IP {} 的注册限制", clientIp);
        } catch (Exception e) {
            log.error("清除注册限制失败，IP: {}, 错误: {}", clientIp, e.getMessage());
        }
    }
} 