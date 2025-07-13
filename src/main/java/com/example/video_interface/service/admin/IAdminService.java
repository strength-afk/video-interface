package com.example.video_interface.service.admin;
import com.example.video_interface.dto.admin.AdminLoginRequest;
import com.example.video_interface.dto.admin.AdminManagementRequest;
import com.example.video_interface.model.User;
import java.util.Map;

/**
 * 管理员服务接口
 * 提供管理员相关的业务操作，包括登录、登出、获取个人信息、管理员管理等
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

    /**
     * 获取管理员列表
     * @param request 查询请求参数
     * @return 管理员列表分页数据
     */
    Map<String, Object> getAdminList(AdminManagementRequest request);

    /**
     * 创建管理员
     * @param request 创建请求参数
     * @return 创建结果
     */
    Map<String, Object> createAdmin(AdminManagementRequest request);

    /**
     * 更新管理员
     * @param request 更新请求参数
     * @return 更新结果
     */
    Map<String, Object> updateAdmin(AdminManagementRequest request);

    /**
     * 删除管理员
     * @param request 删除请求参数
     * @return 删除结果
     */
    Map<String, Object> deleteAdmin(AdminManagementRequest request);

    /**
     * 启用/禁用管理员
     * @param request 状态更新请求参数
     * @return 更新结果
     */
    Map<String, Object> toggleAdminStatus(AdminManagementRequest request);
} 