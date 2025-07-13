package com.example.video_interface.service.admin.impl;

import com.example.video_interface.dto.admin.AdminMovieCategoryDTO;
import com.example.video_interface.model.MovieCategory;
import com.example.video_interface.repository.MovieCategoryRepository;
import com.example.video_interface.service.admin.IAdminMovieCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理后台电影分类服务实现类
 */
@Service
@Transactional
public class AdminMovieCategoryServiceImpl implements IAdminMovieCategoryService {

    @Autowired
    private MovieCategoryRepository movieCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AdminMovieCategoryDTO> getAllCategories() {
        return movieCategoryRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdminMovieCategoryDTO getCategoryById(Long id) {
        return movieCategoryRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public AdminMovieCategoryDTO createCategory(AdminMovieCategoryDTO categoryDTO) {
        MovieCategory category = new MovieCategory();
        BeanUtils.copyProperties(categoryDTO, category, "id", "createdAt", "updatedAt");
        category = movieCategoryRepository.save(category);
        return convertToDTO(category);
    }

    @Override
    public AdminMovieCategoryDTO updateCategory(Long id, AdminMovieCategoryDTO categoryDTO) {
        return movieCategoryRepository.findById(id)
                .map(category -> {
                    BeanUtils.copyProperties(categoryDTO, category, "id", "createdAt", "updatedAt");
                    category = movieCategoryRepository.save(category);
                    return convertToDTO(category);
                })
                .orElse(null);
    }

    @Override
    public void deleteCategory(Long id) {
        MovieCategory category = movieCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        // 检查是否有电影引用该分类
        long movieCount = movieCategoryRepository.countMoviesByCategoryId(id);
        if (movieCount > 0) {
            throw new RuntimeException("无法删除该分类，已有 " + movieCount + " 部电影使用此分类");
        }
        
        movieCategoryRepository.deleteById(id);
    }

    @Override
    public AdminMovieCategoryDTO toggleCategoryStatus(Long id, Boolean enabled) {
        return movieCategoryRepository.findById(id)
                .map(category -> {
                    category.setEnabled(enabled);
                    category = movieCategoryRepository.save(category);
                    return convertToDTO(category);
                })
                .orElse(null);
    }

    /**
     * 将实体转换为DTO
     * @param category 分类实体
     * @return 分类DTO
     */
    private AdminMovieCategoryDTO convertToDTO(MovieCategory category) {
        AdminMovieCategoryDTO dto = new AdminMovieCategoryDTO();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }
} 