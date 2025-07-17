package com.example.video_interface.controller.common;

import com.example.video_interface.dto.common.SystemConfigDTO;
import com.example.video_interface.service.common.ISystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 系统设置控制器
 * 仅管理员可操作
 */
@RestController
@RequestMapping("/common/system-config")
@RequiredArgsConstructor
public class SystemConfigController {
    private final ISystemConfigService systemConfigService;

    /**
     * 获取系统设置（需管理员登录）
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public SystemConfigDTO getConfig() {
        return systemConfigService.getSystemConfig();
    }

    /**
     * 保存系统设置（需管理员登录）
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public void saveConfig(@RequestBody SystemConfigDTO dto) {
        systemConfigService.saveSystemConfig(dto);
    }
} 