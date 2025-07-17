package com.example.video_interface.dto.h5;

import com.example.video_interface.model.UserMoviePurchase;
import lombok.Data;

import java.math.BigDecimal;

/**
 * H5端电影购买请求DTO
 */
@Data
public class H5MoviePurchaseRequest {
    
    /**
     * 电影ID
     */
    private Long movieId;
    
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
     * 订单ID（可选，系统生成）
     */
    private String orderId;
    
    /**
     * 备注
     */
    private String remark;
} 