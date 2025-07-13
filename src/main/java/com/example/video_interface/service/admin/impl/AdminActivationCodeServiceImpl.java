package com.example.video_interface.service.admin.impl;

import com.example.video_interface.dto.admin.AdminActivationCodeDTO;
import com.example.video_interface.model.ActivationCode;
import com.example.video_interface.repository.ActivationCodeRepository;
import com.example.video_interface.service.admin.IAdminActivationCodeService;
import com.example.video_interface.util.RequestContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理后台激活码服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminActivationCodeServiceImpl implements IAdminActivationCodeService {
    
    private final ActivationCodeRepository activationCodeRepository;
    
    @Override
    public Page<AdminActivationCodeDTO> getActivationCodeList(String codeType, String codeStatus, 
                                                             String batchNumber, String keyword, Pageable pageable) {
        log.debug("查询激活码列表 - 类型: {}, 状态: {}, 批次: {}, 关键词: {}", codeType, codeStatus, batchNumber, keyword);
        
        ActivationCode.CodeType type = null;
        if (codeType != null && !codeType.isEmpty()) {
            try {
                type = ActivationCode.CodeType.valueOf(codeType.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("无效的激活码类型: {}", codeType);
            }
        }
        
        ActivationCode.CodeStatus status = null;
        if (codeStatus != null && !codeStatus.isEmpty()) {
            try {
                status = ActivationCode.CodeStatus.valueOf(codeStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("无效的激活码状态: {}", codeStatus);
            }
        }
        
        Page<ActivationCode> activationCodes = activationCodeRepository.findByConditions(type, status, batchNumber, keyword, pageable);
        return activationCodes.map(AdminActivationCodeDTO::fromEntity);
    }
    
    @Override
    public AdminActivationCodeDTO getActivationCodeById(Long id) {
        log.debug("获取激活码详情 - ID: {}", id);
        
        ActivationCode activationCode = activationCodeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("激活码不存在"));
        
        return AdminActivationCodeDTO.fromEntity(activationCode);
    }
    

    
    @Override
    @Transactional
    public Map<String, Object> batchCreateActivationCodes(Map<String, Object> params) {
        log.debug("批量创建激活码 - 参数: {}", params);
        
        String codeType = (String) params.get("codeType");
        
        // 处理创建数量，支持多种数据类型
        Integer count = null;
        Object countObj = params.get("count");
        if (countObj != null) {
            if (countObj instanceof Integer) {
                count = (Integer) countObj;
            } else if (countObj instanceof Number) {
                count = ((Number) countObj).intValue();
            } else if (countObj instanceof String) {
                try {
                    count = Integer.valueOf((String) countObj);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("创建数量格式无效");
                }
            }
        }
        String batchNumber = (String) params.get("batchNumber");
        String remark = (String) params.get("remark");
        
        // 处理VIP时长，支持多种数据类型
        Integer vipDuration = null;
        Object vipDurationObj = params.get("vipDuration");
        if (vipDurationObj != null) {
            if (vipDurationObj instanceof Integer) {
                vipDuration = (Integer) vipDurationObj;
            } else if (vipDurationObj instanceof Number) {
                vipDuration = ((Number) vipDurationObj).intValue();
            } else if (vipDurationObj instanceof String) {
                try {
                    vipDuration = Integer.valueOf((String) vipDurationObj);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("VIP时长格式无效");
                }
            }
        }
        
        // 处理充值金额，支持多种数据类型
        BigDecimal rechargeAmount = null;
        Object rechargeAmountObj = params.get("rechargeAmount");
        if (rechargeAmountObj != null) {
            if (rechargeAmountObj instanceof BigDecimal) {
                rechargeAmount = (BigDecimal) rechargeAmountObj;
            } else if (rechargeAmountObj instanceof Number) {
                rechargeAmount = new BigDecimal(rechargeAmountObj.toString());
            } else if (rechargeAmountObj instanceof String) {
                try {
                    rechargeAmount = new BigDecimal((String) rechargeAmountObj);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("充值金额格式无效");
                }
            }
        }
        
        // 处理激活码长度，支持多种数据类型
        Integer codeLength = null;
        Object codeLengthObj = params.get("codeLength");
        log.debug("接收到的激活码长度参数: {} (类型: {})", codeLengthObj, codeLengthObj != null ? codeLengthObj.getClass().getSimpleName() : "null");
        if (codeLengthObj != null) {
            if (codeLengthObj instanceof Integer) {
                codeLength = (Integer) codeLengthObj;
            } else if (codeLengthObj instanceof Number) {
                codeLength = ((Number) codeLengthObj).intValue();
            } else if (codeLengthObj instanceof String) {
                try {
                    codeLength = Integer.valueOf((String) codeLengthObj);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("激活码长度格式无效");
                }
            }
        }
        log.debug("处理后的激活码长度: {}", codeLength);
        
        // 处理过期天数，支持多种数据类型
        Integer expireDays = null;
        Object expireDaysObj = params.get("expireDays");
        if (expireDaysObj != null) {
            if (expireDaysObj instanceof Integer) {
                expireDays = (Integer) expireDaysObj;
            } else if (expireDaysObj instanceof Number) {
                expireDays = ((Number) expireDaysObj).intValue();
            } else if (expireDaysObj instanceof String) {
                try {
                    expireDays = Integer.valueOf((String) expireDaysObj);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("过期天数格式无效");
                }
            }
        }
        
        if (codeType == null || count == null || count <= 0) {
            throw new IllegalArgumentException("参数无效");
        }
        
        ActivationCode.CodeType type;
        try {
            type = ActivationCode.CodeType.valueOf(codeType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的激活码类型");
        }
        
        // 验证类型相关参数
        if (type == ActivationCode.CodeType.VIP && (vipDuration == null || vipDuration <= 0)) {
            throw new IllegalArgumentException("VIP激活码必须设置时长");
        }
        if (type == ActivationCode.CodeType.RECHARGE && (rechargeAmount == null || rechargeAmount.compareTo(BigDecimal.ZERO) <= 0)) {
            throw new IllegalArgumentException("充值激活码必须设置金额");
        }
        
        List<ActivationCode> activationCodes = new ArrayList<>();
        Long currentUserId = getCurrentUserId();
        LocalDateTime expireAt = LocalDateTime.now().plusDays(expireDays != null ? expireDays : 30);
        
        for (int i = 0; i < count; i++) {
            int finalLength = codeLength != null ? codeLength : 16;
            log.debug("生成第 {} 个激活码，使用长度: {}", i + 1, finalLength);
            String code = generateActivationCode(codeType, finalLength);
            
            ActivationCode activationCode = ActivationCode.builder()
                    .code(code)
                    .codeType(type)
                    .codeStatus(ActivationCode.CodeStatus.UNUSED)
                    .vipDuration(vipDuration)
                    .rechargeAmount(rechargeAmount)
                    .batchNumber(batchNumber)
                    .remark(remark)
                    .createdBy(currentUserId)
                    .expireAt(expireAt)
                    .build();
            
            activationCodes.add(activationCode);
        }
        
        List<ActivationCode> savedActivationCodes = activationCodeRepository.saveAll(activationCodes);
        log.info("批量创建激活码成功 - 数量: {}, 批次: {}", savedActivationCodes.size(), batchNumber);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", savedActivationCodes.size());
        result.put("batchNumber", batchNumber);
        result.put("codes", savedActivationCodes.stream().map(ActivationCode::getCode).collect(Collectors.toList()));
        
        return result;
    }
    
    @Override
    @Transactional
    public AdminActivationCodeDTO updateActivationCode(AdminActivationCodeDTO activationCodeDTO) {
        log.debug("更新激活码 - ID: {}", activationCodeDTO.getId());
        
        ActivationCode existingActivationCode = activationCodeRepository.findById(activationCodeDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("激活码不存在"));
        
        // 检查是否已被使用
        if (existingActivationCode.getCodeStatus() == ActivationCode.CodeStatus.USED) {
            throw new IllegalArgumentException("已使用的激活码不能修改");
        }
        
        // 更新允许修改的字段
        existingActivationCode.setRemark(activationCodeDTO.getRemark());
        existingActivationCode.setExpireAt(activationCodeDTO.getExpireAt());
        existingActivationCode.setCodeStatus(activationCodeDTO.getCodeStatus());
        
        ActivationCode updatedActivationCode = activationCodeRepository.save(existingActivationCode);
        log.info("激活码更新成功 - ID: {}", updatedActivationCode.getId());
        
        return AdminActivationCodeDTO.fromEntity(updatedActivationCode);
    }
    
    @Override
    @Transactional
    public boolean deleteActivationCode(Long id) {
        log.debug("删除激活码 - ID: {}", id);
        
        ActivationCode activationCode = activationCodeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("激活码不存在"));
        
        // 检查是否已被使用
        if (activationCode.getCodeStatus() == ActivationCode.CodeStatus.USED) {
            throw new IllegalArgumentException("已使用的激活码不能删除");
        }
        
        activationCodeRepository.delete(activationCode);
        log.info("激活码删除成功 - ID: {}", id);
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean batchDeleteActivationCodes(List<Long> ids) {
        log.debug("批量删除激活码 - IDs: {}", ids);
        
        List<ActivationCode> activationCodes = activationCodeRepository.findAllById(ids);
        
        // 检查是否有已使用的激活码
        List<ActivationCode> usedCodes = activationCodes.stream()
                .filter(code -> code.getCodeStatus() == ActivationCode.CodeStatus.USED)
                .collect(Collectors.toList());
        
        if (!usedCodes.isEmpty()) {
            throw new IllegalArgumentException("存在已使用的激活码，无法删除");
        }
        
        activationCodeRepository.deleteAll(activationCodes);
        log.info("批量删除激活码成功 - 数量: {}", activationCodes.size());
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean toggleActivationCodeStatus(Long id) {
        log.debug("切换激活码状态 - ID: {}", id);
        
        ActivationCode activationCode = activationCodeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("激活码不存在"));
        
        if (activationCode.getCodeStatus() == ActivationCode.CodeStatus.USED) {
            throw new IllegalArgumentException("已使用的激活码不能修改状态");
        }
        
        if (activationCode.getCodeStatus() == ActivationCode.CodeStatus.UNUSED) {
            activationCode.setCodeStatus(ActivationCode.CodeStatus.DISABLED);
        } else if (activationCode.getCodeStatus() == ActivationCode.CodeStatus.DISABLED) {
            activationCode.setCodeStatus(ActivationCode.CodeStatus.UNUSED);
        }
        
        activationCodeRepository.save(activationCode);
        log.info("激活码状态切换成功 - ID: {}, 新状态: {}", id, activationCode.getCodeStatus());
        
        return true;
    }
    
    @Override
    public Map<String, Object> getActivationCodeStatistics() {
        log.debug("获取激活码统计信息");
        
        Map<String, Object> statistics = new HashMap<>();
        
        // 统计各状态数量
        List<Object[]> statusCounts = activationCodeRepository.countByStatus();
        Map<String, Long> statusStats = new HashMap<>();
        for (Object[] result : statusCounts) {
            String status = result[0].toString();
            Long count = (Long) result[1];
            statusStats.put(status, count);
        }
        statistics.put("statusStats", statusStats);
        
        // 统计各类型数量
        List<Object[]> typeCounts = activationCodeRepository.countByType();
        Map<String, Long> typeStats = new HashMap<>();
        for (Object[] result : typeCounts) {
            String type = result[0].toString();
            Long count = (Long) result[1];
            typeStats.put(type, count);
        }
        statistics.put("typeStats", typeStats);
        
        // 统计总数
        long totalCount = activationCodeRepository.count();
        statistics.put("totalCount", totalCount);
        
        return statistics;
    }
    
    @Override
    public Map<String, Object> getActivationCodeStatisticsByType(String codeType) {
        log.debug("获取激活码统计信息 - 类型: {}", codeType);
        
        Map<String, Object> statistics = new HashMap<>();
        
        ActivationCode.CodeType type = null;
        if (codeType != null && !codeType.isEmpty()) {
            try {
                type = ActivationCode.CodeType.valueOf(codeType.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("无效的激活码类型: {}", codeType);
            }
        }
        
        if (type != null) {
            // 统计指定类型的各状态数量
            Map<String, Long> statusStats = new HashMap<>();
            for (ActivationCode.CodeStatus status : ActivationCode.CodeStatus.values()) {
                long count = activationCodeRepository.countByCodeTypeAndCodeStatus(type, status);
                statusStats.put(status.name(), count);
            }
            statistics.put("statusStats", statusStats);
            
            // 统计指定类型的总数
            long totalCount = activationCodeRepository.countByCodeType(type);
            statistics.put("totalCount", totalCount);
            
            // 添加类型信息
            statistics.put("codeType", type.name());
            statistics.put("codeTypeDesc", type.getDescription());
        } else {
            // 如果没有指定类型，返回所有统计信息
            return getActivationCodeStatistics();
        }
        
        return statistics;
    }
    
    @Override
    public List<AdminActivationCodeDTO> exportActivationCodes(String codeType, String codeStatus, String batchNumber) {
        log.debug("导出激活码 - 类型: {}, 状态: {}, 批次: {}", codeType, codeStatus, batchNumber);
        
        ActivationCode.CodeType type = null;
        if (codeType != null && !codeType.isEmpty()) {
            try {
                type = ActivationCode.CodeType.valueOf(codeType.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("无效的激活码类型: {}", codeType);
            }
        }
        
        ActivationCode.CodeStatus status = null;
        if (codeStatus != null && !codeStatus.isEmpty()) {
            try {
                status = ActivationCode.CodeStatus.valueOf(codeStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("无效的激活码状态: {}", codeStatus);
            }
        }
        
        Page<ActivationCode> activationCodes = activationCodeRepository.findByConditions(type, status, batchNumber, null, Pageable.unpaged());
        return activationCodes.getContent().stream()
                .map(AdminActivationCodeDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public String generateActivationCode(String codeType, int length) {
        log.debug("生成激活码 - 类型: {}, 随机字符长度: {}", codeType, length);
        
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        
        // 添加类型前缀
        if ("VIP".equals(codeType)) {
            code.append("VIP");
        } else if ("RECHARGE".equals(codeType)) {
            code.append("RC");
        }
        
        // 生成指定长度的随机字符（不包括前缀）
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        String generatedCode = code.toString();
        log.debug("生成激活码: {} (总长度: {})", generatedCode, generatedCode.length());
        
        return generatedCode;
    }
    
    @Override
    public boolean validateActivationCode(String code) {
        if (code == null || code.length() < 8 || code.length() > 32) {
            return false;
        }
        
        // 检查是否只包含字母和数字
        return code.matches("^[A-Z0-9]+$");
    }
    
    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        // 这里需要根据实际的用户认证逻辑获取当前用户ID
        // 暂时返回null，实际使用时需要实现
        return null;
    }
} 