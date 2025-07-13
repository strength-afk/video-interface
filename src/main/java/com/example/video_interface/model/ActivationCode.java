package com.example.video_interface.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 激活码实体类
 * 用于管理会员激活码和充值激活码
 */
@Data
@Entity
@Table(name = "activation_codes")
@Comment("激活码表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationCode {
    
    /**
     * 激活码类型枚举
     */
    public enum CodeType {
        VIP("会员激活码"),
        RECHARGE("充值激活码");
        
        private final String description;
        
        CodeType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 激活码状态枚举
     */
    public enum CodeStatus {
        UNUSED("未使用"),
        USED("已使用"),
        EXPIRED("已过期"),
        DISABLED("已禁用");
        
        private final String description;
        
        CodeStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("激活码ID，主键，自增")
    private Long id;
    
    @Column(nullable = false, length = 32, unique = true)
    @Comment("激活码，唯一，不可为空")
    private String code;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "code_type", nullable = false)
    @Comment("激活码类型：VIP-会员激活码，RECHARGE-充值激活码")
    private CodeType codeType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "code_status", nullable = false)
    @Comment("激活码状态：UNUSED-未使用，USED-已使用，EXPIRED-已过期，DISABLED-已禁用")
    private CodeStatus codeStatus = CodeStatus.UNUSED;
    
    @Column(name = "vip_duration")
    @Comment("VIP时长（天），仅会员激活码有效")
    private Integer vipDuration;
    
    @Column(name = "recharge_amount", precision = 10, scale = 2)
    @Comment("充值金额，仅充值激活码有效")
    private BigDecimal rechargeAmount;
    
    @Column(name = "used_by")
    @Comment("使用用户ID")
    private Long usedBy;
    
    @Column(name = "used_at")
    @Comment("使用时间")
    private LocalDateTime usedAt;
    
    @Column(name = "expire_at")
    @Comment("过期时间")
    private LocalDateTime expireAt;
    
    @Column(name = "batch_number", length = 50)
    @Comment("批次号，用于批量管理")
    private String batchNumber;
    
    @Column(length = 200)
    @Comment("备注信息")
    private String remark;
    
    @Column(name = "created_by")
    @Comment("创建人ID")
    private Long createdBy;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @Comment("创建时间")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    @Comment("更新时间")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (codeStatus == null) codeStatus = CodeStatus.UNUSED;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 