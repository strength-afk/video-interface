package com.example.video_interface.service.admin;

import com.example.video_interface.dto.admin.AdminRegionDTO;

import java.util.List;

/**
 * 管理后台地区服务接口
 */
public interface IAdminRegionService {
    
    /**
     * 获取所有地区列表
     * @return 地区列表
     */
    List<AdminRegionDTO> getAllRegions();
    
    /**
     * 根据ID获取地区详情
     * @param id 地区ID
     * @return 地区详情
     */
    AdminRegionDTO getRegionById(Long id);
    
    /**
     * 创建地区
     * @param regionDTO 地区信息
     * @return 创建后的地区
     */
    AdminRegionDTO createRegion(AdminRegionDTO regionDTO);
    
    /**
     * 更新地区
     * @param id 地区ID
     * @param regionDTO 地区信息
     * @return 更新后的地区
     */
    AdminRegionDTO updateRegion(Long id, AdminRegionDTO regionDTO);
    
    /**
     * 删除地区
     * @param id 地区ID
     */
    void deleteRegion(Long id);
    
    /**
     * 切换地区状态
     * @param id 地区ID
     * @param enabled 启用状态
     * @return 更新后的地区
     */
    AdminRegionDTO toggleRegionStatus(Long id, Boolean enabled);
} 