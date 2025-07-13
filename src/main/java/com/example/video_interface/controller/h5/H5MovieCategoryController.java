package com.example.video_interface.controller.h5;

import com.example.video_interface.dto.h5.H5MovieCategoryDTO;
import com.example.video_interface.service.h5.IH5MovieCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * H5端电影分类控制器
 */
@Slf4j
@RestController
@RequestMapping("/h5/categories")
@RequiredArgsConstructor
public class H5MovieCategoryController {

    private final IH5MovieCategoryService movieCategoryService;

    /**
     * 获取所有启用的分类列表
     * @return 分类列表
     */
    @GetMapping("/list")
    public ResponseEntity<?> getAllCategories() {
        try {
            List<H5MovieCategoryDTO> categories = movieCategoryService.getAllEnabledCategories();
            log.debug("获取分类列表成功，共{}个分类", categories.size());
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.error("获取分类列表失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取分类列表失败，请稍后重试"
            ));
        }
    }

    /**
     * 获取分类详情
     * @param id 分类ID
     * @return 分类详情
     */
    @GetMapping("/detail")
    public ResponseEntity<?> getCategoryById(@RequestParam Long id) {
        try {
            H5MovieCategoryDTO category = movieCategoryService.getCategoryById(id);
            log.debug("获取分类详情成功: {}", category.getName());
            return ResponseEntity.ok(category);
        } catch (IllegalArgumentException e) {
            log.warn("获取分类详情失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("获取分类详情失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取分类详情失败，请稍后重试"
            ));
        }
    }
} 