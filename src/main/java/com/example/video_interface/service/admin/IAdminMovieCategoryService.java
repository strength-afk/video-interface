package com.example.video_interface.service.admin;

import com.example.video_interface.dto.admin.AdminMovieCategoryDTO;
import java.util.List;

/**
 * 管理后台电影分类服务接口
 */
public interface IAdminMovieCategoryService {
    
    /**
     * 获取所有分类列表
     * @return 分类列表
     */
    List<AdminMovieCategoryDTO> getAllCategories();
    
    /**
     * 根据ID获取分类详情
     * @param id 分类ID
     * @return 分类详情
     */
    AdminMovieCategoryDTO getCategoryById(Long id);
    
    /**
     * 创建分类
     * @param categoryDTO 分类信息
     * @return 创建后的分类
     */
    AdminMovieCategoryDTO createCategory(AdminMovieCategoryDTO categoryDTO);
    
    /**
     * 更新分类
     * @param id 分类ID
     * @param categoryDTO 分类信息
     * @return 更新后的分类
     */
    AdminMovieCategoryDTO updateCategory(Long id, AdminMovieCategoryDTO categoryDTO);
    
    /**
     * 删除分类
     * @param id 分类ID
     */
    void deleteCategory(Long id);
    
    /**
     * 启用/禁用分类
     * @param id 分类ID
     * @param enabled 是否启用
     * @return 更新后的分类
     */
    AdminMovieCategoryDTO toggleCategoryStatus(Long id, Boolean enabled);
} 