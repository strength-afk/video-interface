package com.example.video_interface.dto.h5;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * H5端电影播放权限响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class H5MoviePlayResponse {
    
    /**
     * 权限状态：ALLOWED-允许播放，LOGIN_REQUIRED-需要登录，VIP_REQUIRED-需要VIP，NOT_ALLOWED-不允许播放
     */
    private String permission;
    
    /**
     * 权限描述
     */
    private String permissionDesc;
    
    /**
     * 是否可以播放
     */
    private Boolean canPlay;
    
    /**
     * 播放URL
     */
    private String playUrl;
    
    /**
     * 试看时长（秒）
     */
    private Integer trialDuration;
    
    /**
     * 是否为试看模式
     */
    private Boolean isTrial;
    
    /**
     * 试看结束时间（秒）
     */
    private Integer trialEndTime;
    
    /**
     * 收费类型
     */
    private String chargeType;
    
    /**
     * 收费类型描述
     */
    private String chargeTypeDesc;
    
    /**
     * 价格（VIP电影单片购买价格）
     */
    private String price;
    
    /**
     * 错误信息
     */
    private String errorMessage;
} 