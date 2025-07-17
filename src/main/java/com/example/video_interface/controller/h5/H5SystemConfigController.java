package com.example.video_interface.controller.h5;

import com.example.video_interface.dto.common.SystemConfigDTO;
import com.example.video_interface.service.common.ISystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * H5端系统配置控制器
 * 提供公开的系统配置获取接口
 */
@Slf4j
@RestController
@RequestMapping("/h5/system-config")
@RequiredArgsConstructor
public class H5SystemConfigController {
    
    private final ISystemConfigService systemConfigService;

    /**
     * 获取系统配置信息（公开接口，无需登录）
     * @return 系统配置信息
     */
    @GetMapping("/info")
    public ResponseEntity<?> getSystemConfig() {
        try {
            SystemConfigDTO config = systemConfigService.getSystemConfig();
            if (config == null) {
                // 如果没有配置，返回默认值
                SystemConfigDTO defaultConfig = new SystemConfigDTO();
                defaultConfig.setSiteName("久伴视频");
                defaultConfig.setVersion("v1.0.0");
                defaultConfig.setCopyright("© 2024 久伴团队");
                return ResponseEntity.ok(defaultConfig);
            }
            
            log.debug("获取系统配置成功");
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            log.error("获取系统配置失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取系统配置失败，请稍后重试"
            ));
        }
    }
} 