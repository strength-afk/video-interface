package com.example.video_interface.service.common;

/**
 * H5登录失败服务接口
 * 提供H5端登录失败次数管理功能
 */
public interface IH5LoginFailureService {
    /**
     * 记录登录失败
     * @param ip 用户IP地址
     * @return 是否需要验证码
     */
    boolean recordLoginFailure(String ip);

    /**
     * 检查是否需要验证码
     * @param ip 用户IP地址
     * @return 是否需要验证码
     */
    boolean needCaptcha(String ip);

    /**
     * 重置登录失败次数
     * @param ip 用户IP地址
     */
    void resetLoginFailures(String ip);
} 