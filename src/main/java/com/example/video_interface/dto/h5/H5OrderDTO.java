package com.example.video_interface.dto.h5;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * H5端订单DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class H5OrderDTO {
    
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 订单类型
     */
    private String orderType;
    
    /**
     * 订单类型描述
     */
    private String orderTypeDesc;
    
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
    private String paymentMethod;
    
    /**
     * 支付方式描述
     */
    private String paymentMethodDesc;
    
    /**
     * 订单状态
     */
    private String orderStatus;
    
    /**
     * 订单状态描述
     */
    private String orderStatusDesc;
    
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