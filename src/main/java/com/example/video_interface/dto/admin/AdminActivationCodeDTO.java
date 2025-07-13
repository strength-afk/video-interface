package com.example.video_interface.dto.admin;

import com.example.video_interface.model.ActivationCode;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理后台激活码DTO
 */
@Data
public class AdminActivationCodeDTO {
    
    private Long id;
    
    /**
     * 激活码
     */
    private String code;
    
    /**
     * 激活码类型
     */
    private ActivationCode.CodeType codeType;
    
    /**
     * 激活码类型描述
     */
    private String codeTypeDesc;
    
    /**
     * 激活码状态
     */
    private ActivationCode.CodeStatus codeStatus;
    
    /**
     * 激活码状态描述
     */
    private String codeStatusDesc;
    
    /**
     * VIP时长（天）
     */
    private Integer vipDuration;
    
    /**
     * 充值金额
     */
    private BigDecimal rechargeAmount;
    
    /**
     * 使用用户ID
     */
    private Long usedBy;
    
    /**
     * 使用时间
     */
    private LocalDateTime usedAt;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireAt;
    
    /**
     * 批次号
     */
    private String batchNumber;
    
    /**
     * 备注信息
     */
    private String remark;
    
    /**
     * 创建人ID
     */
    private Long createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 从ActivationCode实体转换为DTO
     */
    public static AdminActivationCodeDTO fromEntity(ActivationCode activationCode) {
        AdminActivationCodeDTO dto = new AdminActivationCodeDTO();
        dto.setId(activationCode.getId());
        dto.setCode(activationCode.getCode());
        dto.setCodeType(activationCode.getCodeType());
        dto.setCodeTypeDesc(activationCode.getCodeType().getDescription());
        dto.setCodeStatus(activationCode.getCodeStatus());
        dto.setCodeStatusDesc(activationCode.getCodeStatus().getDescription());
        dto.setVipDuration(activationCode.getVipDuration());
        dto.setRechargeAmount(activationCode.getRechargeAmount());
        dto.setUsedBy(activationCode.getUsedBy());
        dto.setUsedAt(activationCode.getUsedAt());
        dto.setExpireAt(activationCode.getExpireAt());
        dto.setBatchNumber(activationCode.getBatchNumber());
        dto.setRemark(activationCode.getRemark());
        dto.setCreatedBy(activationCode.getCreatedBy());
        dto.setCreatedAt(activationCode.getCreatedAt());
        dto.setUpdatedAt(activationCode.getUpdatedAt());
        return dto;
    }
    
    /**
     * 转换为ActivationCode实体
     */
    public ActivationCode toEntity() {
        ActivationCode activationCode = new ActivationCode();
        activationCode.setId(this.id);
        activationCode.setCode(this.code);
        activationCode.setCodeType(this.codeType);
        activationCode.setCodeStatus(this.codeStatus);
        activationCode.setVipDuration(this.vipDuration);
        activationCode.setRechargeAmount(this.rechargeAmount);
        activationCode.setUsedBy(this.usedBy);
        activationCode.setUsedAt(this.usedAt);
        activationCode.setExpireAt(this.expireAt);
        activationCode.setBatchNumber(this.batchNumber);
        activationCode.setRemark(this.remark);
        activationCode.setCreatedBy(this.createdBy);
        return activationCode;
    }
} 