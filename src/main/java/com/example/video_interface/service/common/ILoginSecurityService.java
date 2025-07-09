package com.example.video_interface.service.common;

/**
 * 登录安全服务接口
 * 提供管理员账户锁定相关功能
 */
public interface ILoginSecurityService {
    /**
     * 记录登录失败
     * @param username 用户名
     */
    boolean recordLoginFailure(String username);

    /**
     * 检查账户是否被锁定
     * @param username 用户名
     * @return 是否被锁定
     */
    boolean isAccountLocked(String username);

    /**
     * 重置登录失败次数
     * @param username 用户名
     */
    void resetLoginFailures(String username);

    /**
     * 获取剩余尝试次数
     * @param username 用户名
     */
    int getRemainingAttempts(String username);
} 