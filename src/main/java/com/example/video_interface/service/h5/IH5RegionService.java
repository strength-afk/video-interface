package com.example.video_interface.service.h5;

import com.example.video_interface.dto.h5.H5RegionDTO;

import java.util.List;

/**
 * H5端地区服务接口
 */
public interface IH5RegionService {
    
    /**
     * 获取所有启用的地区列表
     * @return 地区列表
     */
    List<H5RegionDTO> getAllEnabledRegions();
    
    /**
     * 根据ID获取地区详情
     * @param id 地区ID
     * @return 地区详情
     */
    H5RegionDTO getRegionById(Long id);
} 