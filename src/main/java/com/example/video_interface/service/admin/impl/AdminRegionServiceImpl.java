package com.example.video_interface.service.admin.impl;

import com.example.video_interface.dto.admin.AdminRegionDTO;
import com.example.video_interface.model.Region;
import com.example.video_interface.repository.RegionRepository;
import com.example.video_interface.service.admin.IAdminRegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理后台地区服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminRegionServiceImpl implements IAdminRegionService {

    private final RegionRepository regionRepository;

    @Override
    public List<AdminRegionDTO> getAllRegions() {
        return regionRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AdminRegionDTO getRegionById(Long id) {
        return regionRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    @Transactional
    public AdminRegionDTO createRegion(AdminRegionDTO regionDTO) {
        Region region = convertToEntity(regionDTO);
        Region saved = regionRepository.save(region);
        log.info("创建地区成功: {}", saved.getName());
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public AdminRegionDTO updateRegion(Long id, AdminRegionDTO regionDTO) {
        Region existingRegion = regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("地区不存在"));
        
        // 更新字段
        existingRegion.setName(regionDTO.getName());
        existingRegion.setDescription(regionDTO.getDescription());
        existingRegion.setIcon(regionDTO.getIcon());
        existingRegion.setWeight(regionDTO.getWeight());
        existingRegion.setEnabled(regionDTO.getEnabled());
        
        Region updated = regionRepository.save(existingRegion);
        log.info("更新地区成功: {}", updated.getName());
        return convertToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteRegion(Long id) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("地区不存在"));
        
        // 检查是否有电影引用该地区
        long movieCount = regionRepository.countMoviesByRegionId(id);
        if (movieCount > 0) {
            throw new RuntimeException("无法删除该地区，已有 " + movieCount + " 部电影使用此地区");
        }
        
        regionRepository.delete(region);
        log.info("删除地区成功: {}", region.getName());
    }

    @Override
    @Transactional
    public AdminRegionDTO toggleRegionStatus(Long id, Boolean enabled) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("地区不存在"));
        
        region.setEnabled(enabled);
        Region updated = regionRepository.save(region);
        log.info("切换地区状态成功: {} -> {}", updated.getName(), enabled);
        return convertToDTO(updated);
    }

    /**
     * 将实体转换为DTO
     * @param region 地区实体
     * @return 地区DTO
     */
    private AdminRegionDTO convertToDTO(Region region) {
        AdminRegionDTO dto = new AdminRegionDTO();
        BeanUtils.copyProperties(region, dto);
        return dto;
    }

    /**
     * 将DTO转换为实体
     * @param regionDTO 地区DTO
     * @return 地区实体
     */
    private Region convertToEntity(AdminRegionDTO regionDTO) {
        Region region = new Region();
        BeanUtils.copyProperties(regionDTO, region);
        return region;
    }
} 