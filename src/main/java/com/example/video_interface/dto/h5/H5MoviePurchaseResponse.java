package com.example.video_interface.dto.h5;

import com.example.video_interface.model.UserMoviePurchase;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * H5端电影购买响应DTO
 */
@Data
public class H5MoviePurchaseResponse {
    
    /**
     * 购买记录ID
     */
    private Long purchaseId;
    
    /**
     * 电影ID
     */
    private Long movieId;
    
    /**
     * 电影标题
     */
    private String movieTitle;
    
    /**
     * 电影封面
     */
    private String movieCover;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 支付方式
     */
    private UserMoviePurchase.PaymentMethod paymentMethod;
    
    /**
     * 支付金额
     */
    private BigDecimal amount;
    
    /**
     * 购买时间
     */
    private LocalDateTime purchaseTime;
    
    /**
     * 购买状态
     */
    private UserMoviePurchase.PurchaseStatus status;
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 是否购买成功
     */
    private Boolean success;
    
    /**
     * 错误消息
     */
    private String errorMessage;
} 