package com.example.video_interface.service;

import com.example.video_interface.dto.LoginRequest;
import com.example.video_interface.dto.RegisterRequest;
import com.example.video_interface.model.User;

import java.util.Map;
import java.util.Optional;

/**
 * 用户服务接口
 * 提供用户相关的业务操作，包括注册、登录、信息管理等
 */
public interface UserService {
    /**
     * 用户注册
     * @param request 注册请求，包含用户名、密码、邮箱等信息
     * @return 注册成功的用户信息
     * @throws IllegalArgumentException 如果用户名或邮箱已存在
     */
    User registerUser(RegisterRequest request);

    /**
     * 用户登录
     * @param request 登录请求，包含用户名和密码
     * @return 登录成功的用户信息
     * @throws IllegalArgumentException 如果用户名或密码错误
     */
    User loginUser(LoginRequest request);

    /**
     * 为用户生成JWT令牌
     * @param user 用户信息
     * @return JWT令牌
     */
    String generateToken(User user);

    /**
     * 用户登出
     * @param token JWT令牌
     */
    void logout(String token);

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息，如果不存在则返回空
     */
    Optional<User> findByUsername(String username);

    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return true如果用户名已存在，否则返回false
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否已存在
     * @param email 邮箱地址
     * @return true如果邮箱已存在，否则返回false
     */
    boolean existsByEmail(String email);

    /**
     * 更新用户密码
     * @param username 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @throws IllegalArgumentException 如果旧密码错误
     */
    void updatePassword(String username, String oldPassword, String newPassword);

    /**
     * 更新用户基本信息
     * @param username 用户名
     * @param avatar 头像URL
     * @param phoneNumber 手机号码
     * @return 更新后的用户信息
     */
    User updateProfile(String username, String avatar, String phoneNumber);

    /**
     * 获取当前登录用户信息
     * @return 当前用户信息
     */
    User getCurrentUser();

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    User updateUser(User user);

    /**
     * 更新当前用户信息
     * @param userData 用户信息更新数据
     * @return 更新后的用户信息
     */
    User updateCurrentUser(Map<String, String> userData);
}