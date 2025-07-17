package com.example.video_interface.dto.h5;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * H5端充值套餐DTO
 */
@Data
public class H5RechargePackageDTO {
    
    /**
     * 套餐ID
     */
    private Long id;
    
    /**
     * 套餐名称
     */
    private String name;
    
    /**
     * 充值金额
     */
    private BigDecimal amount;
    
    /**
     * 套餐描述
     */
    private String description;
    
    /**
     * 套餐状态 (ACTIVE/INACTIVE)
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 