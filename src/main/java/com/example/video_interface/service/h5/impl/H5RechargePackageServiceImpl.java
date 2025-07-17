package com.example.video_interface.service.h5.impl;

import com.example.video_interface.dto.h5.H5RechargePackageDTO;
import com.example.video_interface.model.RechargePackage;
import com.example.video_interface.repository.RechargePackageRepository;
import com.example.video_interface.service.h5.IH5RechargePackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * H5端充值套餐服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class H5RechargePackageServiceImpl implements IH5RechargePackageService {
    
    private final RechargePackageRepository rechargePackageRepository;
    
    @Override
    public List<H5RechargePackageDTO> getActiveRechargePackages() {
        log.debug("获取所有ACTIVE状态的充值套餐");
        
        List<RechargePackage> activePackages = rechargePackageRepository.findAllActiveOrderByAmount();
        
        return activePackages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将实体转换为DTO
     * @param rechargePackage 充值套餐实体
     * @return 充值套餐DTO
     */
    private H5RechargePackageDTO convertToDTO(RechargePackage rechargePackage) {
        H5RechargePackageDTO dto = new H5RechargePackageDTO();
        dto.setId(rechargePackage.getId());
        dto.setName(rechargePackage.getName());
        dto.setAmount(rechargePackage.getRechargeAmount());
        dto.setDescription(rechargePackage.getDescription());
        dto.setStatus(rechargePackage.getStatus().name());
        dto.setCreatedAt(rechargePackage.getCreatedAt());
        dto.setUpdatedAt(rechargePackage.getUpdatedAt());
        return dto;
    }
} 