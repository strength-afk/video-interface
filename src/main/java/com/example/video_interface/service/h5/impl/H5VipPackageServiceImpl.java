package com.example.video_interface.service.h5.impl;

import com.example.video_interface.dto.admin.AdminVipPackageDTO;
import com.example.video_interface.model.VipPackage;
import com.example.video_interface.repository.VipPackageRepository;
import com.example.video_interface.service.h5.IH5VipPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * H5端VIP套餐Service实现
 */
@Service
@RequiredArgsConstructor
public class H5VipPackageServiceImpl implements IH5VipPackageService {
    private final VipPackageRepository vipPackageRepository;

    @Override
    public List<AdminVipPackageDTO> getActiveVipPackages() {
        List<VipPackage> all = vipPackageRepository.findAll();
        return all.stream()
                .filter(pkg -> pkg.getStatus() == VipPackage.PackageStatus.ACTIVE)
                .map(AdminVipPackageDTO::fromEntity)
                .collect(Collectors.toList());
    }
} 