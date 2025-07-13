package com.example.video_interface.service.h5;

import com.example.video_interface.dto.h5.H5MovieCategoryDTO;
import java.util.List;

/**
 * H5端电影分类服务接口
 */
public interface IH5MovieCategoryService {
    
    /**
     * 获取所有启用的分类列表
     * @return 分类列表
     */
    List<H5MovieCategoryDTO> getAllEnabledCategories();
    
    /**
     * 根据ID获取分类详情
     * @param id 分类ID
     * @return 分类详情
     */
    H5MovieCategoryDTO getCategoryById(Long id);
} 