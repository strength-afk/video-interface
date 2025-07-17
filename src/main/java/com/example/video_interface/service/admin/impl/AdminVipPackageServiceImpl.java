package com.example.video_interface.service.admin.impl;

import com.example.video_interface.dto.admin.AdminVipPackageDTO;
import com.example.video_interface.model.VipPackage;
import com.example.video_interface.repository.VipPackageRepository;
import com.example.video_interface.service.admin.IAdminVipPackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * VIP套餐管理服务实现类
 * 实现后台VIP套餐的所有管理操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminVipPackageServiceImpl implements IAdminVipPackageService {
    private final VipPackageRepository vipPackageRepository;

    /**
     * 分页查询VIP套餐列表
     */
    @Override
    public Page<AdminVipPackageDTO> getVipPackageList(Map<String, Object> params, Pageable pageable) {
        // 这里只做简单的name/status筛选，复杂条件可扩展
        String name = params.getOrDefault("name", "").toString();
        String status = params.getOrDefault("status", "").toString();
        Page<VipPackage> page = vipPackageRepository.findAll((root, query, cb) -> {
            var predicates = cb.conjunction();
            if (name != null && !name.isEmpty()) {
                predicates = cb.and(predicates, cb.like(root.get("name"), "%" + name + "%"));
            }
            if (status != null && !status.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), status));
            } else {
                predicates = cb.and(predicates, cb.notEqual(root.get("status"), VipPackage.PackageStatus.DELETED));
            }
            return predicates;
        }, pageable);
        return new PageImpl<>(
                page.getContent().stream().map(this::toDTO).collect(Collectors.toList()),
                pageable,
                page.getTotalElements()
        );
    }

    /**
     * 获取VIP套餐详情
     */
    @Override
    public AdminVipPackageDTO getVipPackageDetail(Long id) {
        VipPackage entity = vipPackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("套餐不存在"));
        return toDTO(entity);
    }

    /**
     * 创建VIP套餐
     */
    @Override
    @Transactional
    public AdminVipPackageDTO createVipPackage(AdminVipPackageDTO dto) {
        VipPackage entity = VipPackage.builder()
                .name(dto.getName())
                .durationDays(dto.getDurationDays())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .status(VipPackage.PackageStatus.ACTIVE)
                .build();
        VipPackage saved = vipPackageRepository.save(entity);
        return toDTO(saved);
    }

    /**
     * 更新VIP套餐
     */
    @Override
    @Transactional
    public AdminVipPackageDTO updateVipPackage(AdminVipPackageDTO dto) {
        VipPackage entity = vipPackageRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("套餐不存在"));
        entity.setName(dto.getName());
        entity.setDurationDays(dto.getDurationDays());
        entity.setPrice(dto.getPrice());
        entity.setDescription(dto.getDescription());
        // 状态不允许直接改，需用toggleStatus接口
        VipPackage updated = vipPackageRepository.save(entity);
        return toDTO(updated);
    }

    /**
     * 删除VIP套餐（逻辑删除）
     */
    @Override
    @Transactional
    public boolean deleteVipPackage(Long id) {
        Optional<VipPackage> optional = vipPackageRepository.findById(id);
        if (optional.isEmpty()) return false;
        VipPackage entity = optional.get();
        entity.setStatus(VipPackage.PackageStatus.DELETED);
        vipPackageRepository.save(entity);
        return true;
    }

    /**
     * 上下架VIP套餐
     */
    @Override
    @Transactional
    public boolean toggleVipPackageStatus(Long id, String status) {
        Optional<VipPackage> optional = vipPackageRepository.findById(id);
        if (optional.isEmpty()) return false;
        VipPackage entity = optional.get();
        if ("ACTIVE".equalsIgnoreCase(status)) {
            entity.setStatus(VipPackage.PackageStatus.ACTIVE);
        } else if ("INACTIVE".equalsIgnoreCase(status)) {
            entity.setStatus(VipPackage.PackageStatus.INACTIVE);
        } else {
            throw new IllegalArgumentException("状态参数无效");
        }
        vipPackageRepository.save(entity);
        return true;
    }

    @Override
    public List<AdminVipPackageDTO> getAllVipPackages() {
        return vipPackageRepository.findAll().stream()
                .filter(pkg -> pkg.getStatus() != VipPackage.PackageStatus.DELETED)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 实体转DTO
     */
    private AdminVipPackageDTO toDTO(VipPackage entity) {
        return AdminVipPackageDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .durationDays(entity.getDurationDays())
                .price(entity.getPrice())
                .description(entity.getDescription())
                .status(entity.getStatus().name())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
} 