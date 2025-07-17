package com.example.video_interface.dto.h5;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * H5端用户收藏数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class H5UserFavoriteDTO {
    
    /**
     * 收藏ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 内容ID
     */
    private Long contentId;
    
    /**
     * 内容类型
     */
    private String contentType;
    
    /**
     * 内容类型描述
     */
    private String contentTypeDesc;
    
    /**
     * 内容标题
     */
    private String contentTitle;
    
    /**
     * 内容封面
     */
    private String contentCover;
    
    /**
     * 内容封面URL
     */
    private String contentCoverUrl;
    
    /**
     * 评分（电影专用）
     */
    private String rating;
    
    /**
     * 发行年份（电影专用）
     */
    private Integer releaseYear;
    
    /**
     * 排序权重
     */
    private Integer sortOrder;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 