package com.example.video_interface.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@Entity
@Table(name = "orders")
@Comment("订单表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("订单ID，主键，自增")
    private Long id;
    
    @Column(name = "order_no", length = 64, nullable = false, unique = true)
    @Comment("订单号，唯一标识")
    private String orderNo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("用户ID，关联用户表")
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", length = 20, nullable = false)
    @Comment("订单类型：VIP_PURCHASE-VIP购买，MOVIE_PURCHASE-电影购买，ACTIVATION_CODE-激活码购买，RECHARGE-充值")
    private OrderType orderType;
    
    @Column(name = "product_id")
    @Comment("产品ID，关联具体产品（VIP包ID、电影ID、激活码ID等）")
    private Long productId;
    
    @Column(name = "product_name", length = 200)
    @Comment("产品名称")
    private String productName;
    
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    @Comment("订单金额")
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20, nullable = true)
    @Comment("支付方式：BALANCE-余额，WECHAT-微信，ALIPAY-支付宝")
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 20, nullable = false)
    @Comment("订单状态：PENDING-待支付，PAID-已支付，CANCELLED-已取消，REFUNDED-已退款")
    private OrderStatus orderStatus = OrderStatus.PENDING;
    
    @Column(name = "paid_time")
    @Comment("支付时间")
    private LocalDateTime paidTime;
    
    @Column(name = "out_no", length = 64)
    @Comment("第三方订单编号")
    private String outNo;
    
    @Column(name = "pay_no", length = 64)
    @Comment("支付编号")
    private String payNo;
    
    @Column(name = "remark", length = 500)
    @Comment("订单备注")
    private String remark;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @Comment("创建时间")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    @Comment("更新时间")
    private LocalDateTime updatedAt;
    
    /**
     * 订单类型枚举
     */
    public enum OrderType {
        VIP_PURCHASE("VIP购买"),
        MOVIE_PURCHASE("电影购买"),
        MANGA_PURCHASE("漫画购买"),
        NOVEL_PURCHASE("小说购买"),
        ACTIVATION_CODE("激活码购买"),
        RECHARGE("充值");
        
        private final String description;
        
        OrderType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 支付方式枚举
     */
    public enum PaymentMethod {
        BALANCE("余额支付"),
        WECHAT("微信支付"),
        ALIPAY("支付宝");
        
        private final String description;
        
        PaymentMethod(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 订单状态枚举
     */
    public enum OrderStatus {
        PENDING("待支付"),
        PAID("已支付"),
        CANCELLED("已取消"),
        REFUNDED("已退款");
        
        private final String description;
        
        OrderStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (orderStatus == null) {
            orderStatus = OrderStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 