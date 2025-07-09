package com.example.video_interface.service.admin;

import com.example.video_interface.dto.admin.AdminLoginRequest;
import com.example.video_interface.model.User;

/**
 * 管理员服务接口
 * 提供管理员相关的业务操作，包括登录、登出、获取个人信息等
 */
public interface IAdminService {
    /**
     * 管理员登录
     * @param request 登录请求，包含用户名和密码
     * @return 登录成功的管理员信息
     * @throws IllegalArgumentException 如果用户名或密码错误或不是管理员
     */
    User adminLogin(AdminLoginRequest request);

    /**
     * 管理员登出
     * @param token JWT令牌
     */
    void adminLogout(String token);

    /**
     * 获取当前管理员信息
     * @return 当前管理员信息
     */
    User getCurrentAdmin();
} 