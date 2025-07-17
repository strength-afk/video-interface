package com.example.video_interface.dto.admin;

import com.example.video_interface.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理员订单查询请求DTO
 * 用于管理员查询订单列表的各种筛选条件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderRequest {
    /**
     * 查询参数：页码
     */
    private Integer page = 1;
    
    /**
     * 查询参数：每页大小
     */
    private Integer size = 10;
    
    /**
     * 查询参数：搜索关键词（订单号、用户名、邮箱、产品名称）
     */
    private String keyword;
    
    /**
     * 查询参数：订单类型筛选
     */
    private Order.OrderType orderTypeFilter;
    
    /**
     * 查询参数：订单状态筛选
     */
    private Order.OrderStatus orderStatusFilter;
    
    /**
     * 查询参数：支付方式筛选
     */
    private Order.PaymentMethod paymentMethodFilter;
    
    /**
     * 查询参数：用户ID筛选
     */
    private Long userIdFilter;
    
    /**
     * 查询参数：产品ID筛选
     */
    private Long productIdFilter;
    

    
    /**
     * 查询参数：开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 查询参数：结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 查询参数：支付开始时间
     */
    private LocalDateTime paidStartTime;
    
    /**
     * 查询参数：支付结束时间
     */
    private LocalDateTime paidEndTime;
    
    /**
     * 排序字段
     */
    private String sortBy = "createdAt";
    
    /**
     * 排序方向：asc-升序，desc-降序
     */
    private String sortDirection = "desc";
} 