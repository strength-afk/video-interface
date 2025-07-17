package com.example.video_interface.service.h5;

import com.example.video_interface.dto.h5.H5LoginRequest;
import com.example.video_interface.dto.h5.H5RegisterRequest;
import com.example.video_interface.model.User;

import java.util.Map;

/**
 * H5用户服务接口
 * 提供H5端用户相关的业务操作，包括注册、登录、信息管理等
 */
public interface IH5UserService {
    /**
     * 用户注册
     * @param request 注册请求，包含用户名、密码、邮箱等信息
     * @return 注册成功的用户信息
     * @throws IllegalArgumentException 如果用户名或邮箱已存在
     */
    User registerUser(H5RegisterRequest request);

    /**
     * H5用户登录
     * @param request 登录请求，包含用户名、密码、验证码等信息
     * @return 登录结果，包含用户信息和是否需要验证码
     * @throws IllegalArgumentException 如果用户名或密码错误或验证码错误
     */
    Map<String, Object> h5Login(H5LoginRequest request);

    /**
     * 用户登出
     * @param token JWT令牌
     */
    void logout(String token);

    /**
     * 获取当前用户信息
     * @return 当前用户信息
     */
    User getCurrentUser();

    /**
     * 更新当前用户信息
     * @param userData 用户信息更新数据
     * @return 更新后的用户信息
     */
    User updateCurrentUser(Map<String, String> userData);

    /**
     * 激活会员激活码
     * @param activationCode 激活码
     * @param userId 用户ID
     * @return 激活结果信息
     */
    Map<String, Object> activateVipCode(String activationCode, Long userId);

    /**
     * 使用充值激活码充值
     * @param code 充值激活码
     * @param userId 用户ID
     * @return 充值结果信息
     */
    Map<String, Object> rechargeByCode(String code, Long userId);

    /**
     * 修改当前用户密码
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @throws IllegalArgumentException 如果原密码错误或新密码不合法
     */
    void changePassword(String oldPassword, String newPassword);

    /**
     * 绑定邮箱
     * @param email 邮箱地址
     * @param code 验证码
     */
    void bindEmail(String email, String code);
} 