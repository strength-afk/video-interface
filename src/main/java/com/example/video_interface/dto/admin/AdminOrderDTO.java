package com.example.video_interface.dto.admin;

import com.example.video_interface.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理员订单管理DTO
 * 用于管理员查看和管理订单信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderDTO {
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户邮箱
     */
    private String userEmail;
    
    /**
     * 订单类型
     */
    private Order.OrderType orderType;
    
    /**
     * 订单类型描述
     */
    private String orderTypeDescription;
    
    /**
     * 产品ID
     */
    private Long productId;
    
    /**
     * 产品名称
     */
    private String productName;
    
    /**
     * 订单金额
     */
    private BigDecimal amount;
    
    /**
     * 支付方式
     */
    private Order.PaymentMethod paymentMethod;
    
    /**
     * 支付方式描述
     */
    private String paymentMethodDescription;
    
    /**
     * 订单状态
     */
    private Order.OrderStatus orderStatus;
    
    /**
     * 订单状态描述
     */
    private String orderStatusDescription;
    
    /**
     * 支付时间
     */
    private LocalDateTime paidTime;
    
    /**
     * 第三方订单编号
     */
    private String outNo;
    
    /**
     * 支付编号
     */
    private String payNo;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 