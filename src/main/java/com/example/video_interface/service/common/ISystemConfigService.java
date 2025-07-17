package com.example.video_interface.service.common;

import com.example.video_interface.dto.common.SystemConfigDTO;

/**
 * 系统设置服务接口
 * 提供系统全局配置的查询与保存功能
 */
public interface ISystemConfigService {
    /**
     * 获取系统设置（全局唯一）
     * @return 系统设置DTO
     */
    SystemConfigDTO getSystemConfig();

    /**
     * 保存系统设置（全局唯一）
     * @param configDTO 系统设置DTO
     */
    void saveSystemConfig(SystemConfigDTO configDTO);
} 