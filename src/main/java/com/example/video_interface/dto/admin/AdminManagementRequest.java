package com.example.video_interface.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员管理请求DTO
 * 用于管理员管理功能的请求参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminManagementRequest {
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phoneNumber;
    
    /**
     * 用户角色
     */
    private String role;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 页码
     */
    private Integer page;
    
    /**
     * 每页大小
     */
    private Integer size;
} 