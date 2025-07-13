package com.example.video_interface.service.common;

/**
 * 用户自动解锁服务接口
 * 提供定时检查和自动解锁用户账户的功能
 */
public interface IUserAutoUnlockService {
    
    /**
     * 检查并自动解锁到期的用户账户
     * 定时任务调用此方法
     */
    void checkAndAutoUnlockUsers();
    
    /**
     * 手动触发自动解锁检查
     * 用于测试或紧急情况
     */
    void manualUnlockCheck();
} 