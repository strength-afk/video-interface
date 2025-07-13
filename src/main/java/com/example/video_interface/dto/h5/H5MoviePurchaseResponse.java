package com.example.video_interface.dto.h5;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * H5端VIP电影单片购买响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class H5MoviePurchaseResponse {
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 支付金额
     */
    private String amount;
    
    /**
     * 支付二维码URL（用于扫码支付）
     */
    private String qrCodeUrl;
    
    /**
     * 支付链接（用于H5支付）
     */
    private String paymentUrl;
    
    /**
     * 支付状态：PENDING-待支付，SUCCESS-支付成功，FAILED-支付失败
     */
    private String paymentStatus;
} 