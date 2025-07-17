package com.example.video_interface.service.common.impl;

import com.example.video_interface.dto.common.SystemConfigDTO;
import com.example.video_interface.model.SystemConfig;
import com.example.video_interface.repository.SystemConfigRepository;
import com.example.video_interface.service.common.ISystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统设置服务实现
 * 仅支持全局唯一配置
 */
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements ISystemConfigService {
    private final SystemConfigRepository systemConfigRepository;

    /**
     * 获取系统设置（全局唯一，id=1）
     */
    @Override
    public SystemConfigDTO getSystemConfig() {
        SystemConfig config = systemConfigRepository.findById(1L).orElse(null);
        if (config == null) return null;
        SystemConfigDTO dto = new SystemConfigDTO();
        BeanUtils.copyProperties(config, dto);
        return dto;
    }

    /**
     * 保存系统设置（全局唯一，id=1）
     */
    @Override
    @Transactional
    public void saveSystemConfig(SystemConfigDTO configDTO) {
        SystemConfig config = systemConfigRepository.findById(1L).orElse(new SystemConfig());
        BeanUtils.copyProperties(configDTO, config);
        config.setId(1L); // 保证全局唯一
        systemConfigRepository.save(config);
    }
} 