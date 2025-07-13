package com.example.video_interface.dto.h5;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * H5端VIP电影单片购买请求DTO
 */
@Data
public class H5MoviePurchaseRequest {
    
    /**
     * 电影ID
     */
    @NotNull(message = "电影ID不能为空")
    private Long movieId;
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 支付方式：WECHAT-微信支付，ALIPAY-支付宝
     */
    private String paymentMethod = "WECHAT";
} 