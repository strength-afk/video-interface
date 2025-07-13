package com.example.video_interface.dto.h5;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * H5端地区DTO
 * 相比管理后台DTO，移除了敏感字段
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class H5RegionDTO {
    
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
} 