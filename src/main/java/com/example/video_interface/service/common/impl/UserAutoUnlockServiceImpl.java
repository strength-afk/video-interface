package com.example.video_interface.service.common.impl;

import com.example.video_interface.model.User;
import com.example.video_interface.repository.UserRepository;
import com.example.video_interface.service.common.IUserAutoUnlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户自动解锁服务实现类
 * 提供定时检查和自动解锁用户账户的功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAutoUnlockServiceImpl implements IUserAutoUnlockService {
    
    private final UserRepository userRepository;
    
    /**
     * 检查并自动解锁到期的用户账户
     * 定时任务：可配置执行频率
     */
    @Override
    @Scheduled(fixedRateString = "${app.scheduling.auto-unlock.interval:300000}") // 默认5分钟
    @Transactional
    public void checkAndAutoUnlockUsers() {
        checkAndAutoUnlockTasks();
    }
    
    /**
     * 手动触发自动解锁检查
     * 用于测试或紧急情况
     */
    @Override
    @Transactional
    public void manualUnlockCheck() {
        log.info("手动触发用户自动解锁检查");
        checkAndAutoUnlockUsers();
    }
    
    /**
     * 开发环境快速测试任务：每1分钟执行一次
     * 仅在开发环境启用，生产环境自动禁用
     */
    @Profile("dev")
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void devAutoUnlockTask() {
        log.info("=== 开发环境快速测试任务执行 ===");
        checkAndAutoUnlockTasks();
    }
    
    /**
     * 内部方法：执行解锁检查逻辑
     */
    private void checkAndAutoUnlockTasks() {
        log.info("开始执行用户自动解锁检查任务");
        
        try {
            // 查找所有已锁定且解锁时间已到的用户
            List<User> lockedUsers = userRepository.findByIsLockedTrueAndUnlockTimeBefore(LocalDateTime.now());
            
            if (lockedUsers.isEmpty()) {
                log.info("没有需要自动解锁的用户");
                return;
            }
            
            log.info("发现 {} 个用户需要自动解锁", lockedUsers.size());
            
            for (User user : lockedUsers) {
                try {
                    log.info("正在解锁用户: {}, 解锁时间: {}, 当前时间: {}", 
                        user.getUsername(), user.getUnlockTime(), LocalDateTime.now());
                    
                    // 执行自动解锁
                    user.setIsLocked(false);
                    user.setLockReason(null);
                    user.setLockTime(null);
                    user.setUnlockTime(null);
                    user.setFailedLoginAttempts(0);
                    user.setLastFailedLoginTime(null);
                    
                    User savedUser = userRepository.save(user);
                    log.info("用户 {} 已自动解锁", savedUser.getUsername());
                    
                } catch (Exception e) {
                    log.error("自动解锁用户 {} 失败: {}", user.getUsername(), e.getMessage(), e);
                }
            }
            
            log.info("用户自动解锁检查任务完成，成功解锁 {} 个用户", lockedUsers.size());
            
        } catch (Exception e) {
            log.error("执行用户自动解锁检查任务时发生错误: {}", e.getMessage(), e);
        }
    }
} 