package com.example.video_interface.service.admin.impl;

import com.example.video_interface.dto.admin.AdminRechargePackageDTO;
import com.example.video_interface.model.RechargePackage;
import com.example.video_interface.repository.RechargePackageRepository;
import com.example.video_interface.service.admin.IAdminRechargePackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 充值套餐管理服务实现类
 * 实现后台充值套餐的所有管理操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminRechargePackageServiceImpl implements IAdminRechargePackageService {
    
    private final RechargePackageRepository rechargePackageRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Page<AdminRechargePackageDTO> getRechargePackageList(Map<String, Object> params, Pageable pageable) {
        log.info("查询充值套餐列表，参数: {}", params);
        
        String name = (String) params.get("name");
        String status = (String) params.get("status");
        
        Page<RechargePackage> packages;
        
        if (name != null && !name.trim().isEmpty() && status != null && !status.trim().isEmpty()) {
            // 按名称和状态查询
            RechargePackage.PackageStatus packageStatus = RechargePackage.PackageStatus.valueOf(status);
            packages = rechargePackageRepository.findByStatusAndNameContainingIgnoreCase(packageStatus, name, pageable);
        } else if (name != null && !name.trim().isEmpty()) {
            // 仅按名称查询（排除已删除的）
            packages = rechargePackageRepository.findByNameContainingIgnoreCaseAndStatusNot(name, RechargePackage.PackageStatus.DELETED, pageable);
        } else if (status != null && !status.trim().isEmpty()) {
            // 仅按状态查询
            RechargePackage.PackageStatus packageStatus = RechargePackage.PackageStatus.valueOf(status);
            packages = rechargePackageRepository.findByStatus(packageStatus, pageable);
        } else {
            // 查询所有未删除的套餐
            packages = rechargePackageRepository.findByStatusNot(RechargePackage.PackageStatus.DELETED, pageable);
        }
        
        return packages.map(AdminRechargePackageDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AdminRechargePackageDTO getRechargePackageDetail(Long id) {
        log.info("获取充值套餐详情，ID: {}", id);
        
        RechargePackage pkg = rechargePackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("充值套餐不存在，ID: " + id));
        
        return AdminRechargePackageDTO.fromEntity(pkg);
    }
    
    @Override
    public AdminRechargePackageDTO createRechargePackage(AdminRechargePackageDTO dto) {
        log.info("创建充值套餐，名称: {}", dto.getName());
        
        // 检查套餐名称是否已存在
        if (rechargePackageRepository.findByNameContainingIgnoreCase(dto.getName(), Pageable.unpaged()).hasContent()) {
            throw new IllegalArgumentException("套餐名称已存在: " + dto.getName());
        }
        
        RechargePackage pkg = RechargePackage.builder()
                .name(dto.getName())
                .rechargeAmount(dto.getRechargeAmount())
                .description(dto.getDescription())
                .status(RechargePackage.PackageStatus.ACTIVE)
                .build();
        
        RechargePackage saved = rechargePackageRepository.save(pkg);
        log.info("充值套餐创建成功，ID: {}", saved.getId());
        
        return AdminRechargePackageDTO.fromEntity(saved);
    }
    
    @Override
    public AdminRechargePackageDTO updateRechargePackage(AdminRechargePackageDTO dto) {
        log.info("更新充值套餐，ID: {}", dto.getId());
        
        RechargePackage pkg = rechargePackageRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("充值套餐不存在，ID: " + dto.getId()));
        
        // 检查套餐名称是否已被其他套餐使用
        if (!pkg.getName().equals(dto.getName())) {
            if (rechargePackageRepository.findByNameContainingIgnoreCase(dto.getName(), Pageable.unpaged()).hasContent()) {
                throw new IllegalArgumentException("套餐名称已存在: " + dto.getName());
            }
        }
        
        // 检查充值金额是否已被其他套餐使用
        if (!pkg.getRechargeAmount().equals(dto.getRechargeAmount())) {
            List<RechargePackage> existingPackages = rechargePackageRepository.findByRechargeAmount(dto.getRechargeAmount());
            if (!existingPackages.isEmpty()) {
                throw new IllegalArgumentException("充值金额已存在: " + dto.getRechargeAmount());
            }
        }
        
        pkg.setName(dto.getName());
        pkg.setRechargeAmount(dto.getRechargeAmount());
        pkg.setDescription(dto.getDescription());
        
        RechargePackage saved = rechargePackageRepository.save(pkg);
        log.info("充值套餐更新成功，ID: {}", saved.getId());
        
        return AdminRechargePackageDTO.fromEntity(saved);
    }
    
    @Override
    public boolean deleteRechargePackage(Long id) {
        log.info("删除充值套餐，ID: {}", id);
        
        RechargePackage pkg = rechargePackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("充值套餐不存在，ID: " + id));
        
        pkg.setStatus(RechargePackage.PackageStatus.DELETED);
        rechargePackageRepository.save(pkg);
        
        log.info("充值套餐删除成功，ID: {}", id);
        return true;
    }
    
    @Override
    public boolean toggleRechargePackageStatus(Long id, String status) {
        log.info("切换充值套餐状态，ID: {}, 状态: {}", id, status);
        
        RechargePackage pkg = rechargePackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("充值套餐不存在，ID: " + id));
        
        RechargePackage.PackageStatus newStatus = RechargePackage.PackageStatus.valueOf(status);
        pkg.setStatus(newStatus);
        rechargePackageRepository.save(pkg);
        
        log.info("充值套餐状态切换成功，ID: {}, 新状态: {}", id, status);
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AdminRechargePackageDTO> getAllRechargePackages() {
        log.info("获取所有充值套餐");
        
        List<RechargePackage> packages = rechargePackageRepository.findAllActiveOrderByAmount();
        log.info("从数据库查询到 {} 个充值套餐", packages.size());
        
        List<AdminRechargePackageDTO> dtos = packages.stream()
                .map(AdminRechargePackageDTO::fromEntity)
                .collect(Collectors.toList());
        
        log.info("转换为DTO后得到 {} 个充值套餐", dtos.size());
        dtos.forEach(dto -> log.info("充值套餐: ID={}, 名称={}, 金额={}", dto.getId(), dto.getName(), dto.getRechargeAmount()));
        
        return dtos;
    }
} 