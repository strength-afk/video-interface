package com.example.video_interface.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * VIP套餐实体类
 * 用于管理VIP套餐（如月卡、年卡等）
 */
@Data
@Entity
@Table(name = "vip_packages")
@Comment("VIP套餐表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VipPackage {
    /**
     * 套餐状态枚举
     */
    public enum PackageStatus {
        ACTIVE("上架"),
        INACTIVE("下架"),
        DELETED("已删除");
        private final String description;
        PackageStatus(String description) { this.description = description; }
        public String getDescription() { return description; }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("套餐ID，主键，自增")
    private Long id;

    @Column(nullable = false, length = 50)
    @Comment("套餐名称，如月卡、年卡")
    private String name;

    @Column(name = "duration_days", nullable = false)
    @Comment("套餐时长（天）")
    private Integer durationDays;

    @Column(nullable = false, precision = 10, scale = 2)
    @Comment("套餐价格，单位元")
    private BigDecimal price;

    @Column(length = 200)
    @Comment("套餐描述")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Comment("状态：ACTIVE-上架，INACTIVE-下架，DELETED-已删除")
    private PackageStatus status = PackageStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @Comment("创建时间")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Comment("最后更新时间")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = PackageStatus.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 