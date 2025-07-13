package com.example.video_interface.dto.h5;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * H5端电影分类DTO
 * 相比管理后台DTO，移除了敏感字段
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class H5MovieCategoryDTO {
    
    /**
     * 分类ID
     */
    private Long id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 分类描述
     */
    private String description;
    
    /**
     * 分类图标URL
     */
    private String icon;
    
    /**
     * 分类排序权重
     */
    private Integer weight;
} 