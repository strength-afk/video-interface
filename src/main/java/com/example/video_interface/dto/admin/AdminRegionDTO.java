package com.example.video_interface.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 管理后台地区DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRegionDTO {
    
    /**
     * 地区ID
     */
    private Long id;
    
    /**
     * 地区名称
     */
    private String name;
    
    /**
     * 地区描述
     */
    private String description;
    
    /**
     * 地区图标URL
     */
    private String icon;
    
    /**
     * 地区排序权重
     */
    private Integer weight;
    
    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 