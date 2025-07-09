package com.example.video_interface.service.common;

/**
 * 注册限制服务接口
 * 使用Redis存储IP地址的注册次数，防止同一IP短时间内大量注册
 */
public interface IRegistrationLimitService {
    /**
     * 检查IP是否可以注册
     * @param clientIp 客户端IP地址
     * @return true如果可以注册，false如果已达到限制
     */
    boolean canRegister(String clientIp);

    /**
     * 获取当前注册次数
     * @param clientIp 客户端IP地址
     * @return 当前注册次数
     */
    int getCurrentCount(String clientIp);

    /**
     * 记录一次注册
     * @param clientIp 客户端IP地址
     * @return 更新后的注册次数
     */
    int recordRegistration(String clientIp);

    /**
     * 获取剩余可注册次数
     * @param clientIp 客户端IP地址
     * @return 剩余可注册次数
     */
    int getRemainingCount(String clientIp);

    /**
     * 获取限制重置时间（秒）
     * @param clientIp 客户端IP地址
     * @return 限制重置时间（秒），如果没有限制返回0
     */
    long getResetTimeInSeconds(String clientIp);

    /**
     * 手动清除IP的注册限制（仅用于管理员操作）
     * @param clientIp 客户端IP地址
     */
    void clearRegistrationLimit(String clientIp);
} 