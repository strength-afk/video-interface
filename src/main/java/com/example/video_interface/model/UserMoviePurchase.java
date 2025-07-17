package com.example.video_interface.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户电影购买记录实体类
 */
@Data
@Entity
@Table(name = "user_movie_purchases")
@Comment("用户电影购买记录表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMoviePurchase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("购买记录ID，主键，自增")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("用户ID，关联用户表")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    @Comment("电影ID，关联电影表")
    private Movie movie;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20, nullable = false)
    @Comment("支付方式：BALANCE-余额，WECHAT-微信，ALIPAY-支付宝")
    private PaymentMethod paymentMethod;
    
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    @Comment("支付金额")
    private BigDecimal amount;
    
    @Column(name = "purchase_time")
    @Comment("购买时间")
    private LocalDateTime purchaseTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Comment("状态：SUCCESS-成功，FAILED-失败")
    private PurchaseStatus status = PurchaseStatus.SUCCESS;
    
    @Column(name = "order_id", length = 64)
    @Comment("订单ID")
    private String orderId;
    
    @Column(name = "remark", length = 200)
    @Comment("备注")
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
     * 购买状态枚举
     */
    public enum PurchaseStatus {
        SUCCESS("成功"),
        FAILED("失败");
        
        private final String description;
        
        PurchaseStatus(String description) {
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
        if (purchaseTime == null) {
            purchaseTime = LocalDateTime.now();
        }
        if (status == null) {
            status = PurchaseStatus.SUCCESS;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 