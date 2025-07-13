package com.example.video_interface.service.h5.impl;

import com.example.video_interface.dto.h5.H5MovieCategoryDTO;
import com.example.video_interface.model.MovieCategory;
import com.example.video_interface.repository.MovieCategoryRepository;
import com.example.video_interface.service.h5.IH5MovieCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * H5端电影分类服务实现类
 */
@Service
@Transactional(readOnly = true)
public class H5MovieCategoryServiceImpl implements IH5MovieCategoryService {

    @Autowired
    private MovieCategoryRepository movieCategoryRepository;

    @Override
    public List<H5MovieCategoryDTO> getAllEnabledCategories() {
        return movieCategoryRepository.findByEnabledOrderByWeightDesc(true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public H5MovieCategoryDTO getCategoryById(Long id) {
        return movieCategoryRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * 将实体转换为DTO
     * @param category 分类实体
     * @return 分类DTO
     */
    private H5MovieCategoryDTO convertToDTO(MovieCategory category) {
        H5MovieCategoryDTO dto = new H5MovieCategoryDTO();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }
} 