package com.example.video_interface.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.video_interface.model.RechargePackage;

/**
 * 充值套餐管理DTO
 * 用于后台管理充值套餐数据传输
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRechargePackageDTO {
    /** 套餐ID */
    private Long id;
    /** 套餐名称 */
    private String name;
    /** 充值金额 */
    private BigDecimal rechargeAmount;
    /** 套餐描述 */
    private String description;
    /** 状态：ACTIVE-上架，INACTIVE-下架，DELETED-已删除 */
    private String status;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 更新时间 */
    private LocalDateTime updatedAt;

    public static AdminRechargePackageDTO fromEntity(RechargePackage pkg) {
        if (pkg == null) return null;
        return AdminRechargePackageDTO.builder()
                .id(pkg.getId())
                .name(pkg.getName())
                .rechargeAmount(pkg.getRechargeAmount())
                .description(pkg.getDescription())
                .status(pkg.getStatus() != null ? pkg.getStatus().name() : null)
                .createdAt(pkg.getCreatedAt())
                .updatedAt(pkg.getUpdatedAt())
                .build();
    }
} 