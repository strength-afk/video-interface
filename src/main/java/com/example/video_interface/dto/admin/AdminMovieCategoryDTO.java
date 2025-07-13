package com.example.video_interface.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 管理后台电影分类DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminMovieCategoryDTO {
    
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