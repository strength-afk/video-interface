package com.example.video_interface.service.h5.impl;

import com.example.video_interface.dto.h5.H5RegionDTO;
import com.example.video_interface.model.Region;
import com.example.video_interface.repository.RegionRepository;
import com.example.video_interface.service.h5.IH5RegionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * H5端地区服务实现类
 */
@Service
@Transactional(readOnly = true)
public class H5RegionServiceImpl implements IH5RegionService {

    @Autowired
    private RegionRepository regionRepository;

    @Override
    public List<H5RegionDTO> getAllEnabledRegions() {
        return regionRepository.findByEnabledOrderByWeightDesc(true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public H5RegionDTO getRegionById(Long id) {
        return regionRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * 将实体转换为DTO
     * @param region 地区实体
     * @return 地区DTO
     */
    private H5RegionDTO convertToDTO(Region region) {
        H5RegionDTO dto = new H5RegionDTO();
        BeanUtils.copyProperties(region, dto);
        return dto;
    }
} 