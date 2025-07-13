package com.example.video_interface.dto.h5;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * H5端电影播放权限请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class H5MoviePlayRequest {
    
    /**
     * 电影ID
     */
    private Long movieId;
    
    /**
     * 用户ID（可为空，表示未登录用户）
     */
    private Long userId;
    
    /**
     * 播放类型：FULL-完整播放，TRIAL-试看
     */
    private String playType;
} 