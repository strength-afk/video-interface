package com.example.video_interface.controller.admin;

import com.example.video_interface.dto.admin.AdminMovieCategoryDTO;
import com.example.video_interface.service.admin.IAdminMovieCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理后台电影分类控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminMovieCategoryController {

    private final IAdminMovieCategoryService movieCategoryService;

    /**
     * 获取所有分类列表
     * @return 分类列表
     */
    @GetMapping("/list")
    public ResponseEntity<?> getAllCategories() {
        try {
            List<AdminMovieCategoryDTO> categories = movieCategoryService.getAllCategories();
            log.debug("获取分类列表成功，共{}个分类", categories.size());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", categories
            ));
        } catch (Exception e) {
            log.error("获取分类列表失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
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
            AdminMovieCategoryDTO category = movieCategoryService.getCategoryById(id);
            if (category == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "分类不存在"
                ));
            }
            log.debug("获取分类详情成功: {}", category.getName());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", category
            ));
        } catch (Exception e) {
            log.error("获取分类详情失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取分类详情失败，请稍后重试"
            ));
        }
    }

    /**
     * 创建分类
     * @param categoryDTO 分类信息
     * @return 创建后的分类
     */
    @PostMapping("/create")
    public ResponseEntity<?> createCategory(@RequestBody AdminMovieCategoryDTO categoryDTO) {
        try {
            AdminMovieCategoryDTO created = movieCategoryService.createCategory(categoryDTO);
            log.debug("创建分类成功: {}", created.getName());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", created,
                "message", "创建成功"
            ));
        } catch (Exception e) {
            log.error("创建分类失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "创建分类失败，请稍后重试"
            ));
        }
    }

    /**
     * 更新分类
     * @param categoryDTO 分类信息
     * @return 更新后的分类
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateCategory(@RequestBody AdminMovieCategoryDTO categoryDTO) {
        try {
            if (categoryDTO.getId() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "分类ID不能为空"
                ));
            }
            
            AdminMovieCategoryDTO updated = movieCategoryService.updateCategory(categoryDTO.getId(), categoryDTO);
            if (updated == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "分类不存在"
                ));
            }
            
            log.debug("更新分类成功: {}", updated.getName());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", updated,
                "message", "更新成功"
            ));
        } catch (Exception e) {
            log.error("更新分类失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "更新分类失败，请稍后重试"
            ));
        }
    }

    /**
     * 删除分类
     * @param requestBody 包含id的请求体
     * @return 操作结果
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteCategory(@RequestBody Map<String, Object> requestBody) {
        try {
            Long id = Long.valueOf(requestBody.get("id").toString());
            movieCategoryService.deleteCategory(id);
            log.debug("删除分类成功: {}", id);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "删除成功"
            ));
        } catch (Exception e) {
            log.error("删除分类失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "删除分类失败，请稍后重试"
            ));
        }
    }

    /**
     * 更新分类状态
     * @param requestBody 包含id和enabled的请求体
     * @return 更新后的分类
     */
    @PostMapping("/status")
    public ResponseEntity<?> toggleCategoryStatus(@RequestBody Map<String, Object> requestBody) {
        try {
            Long id = Long.valueOf(requestBody.get("id").toString());
            Object enabledObj = requestBody.get("enabled");
            Boolean enabled;
            
            // 处理不同类型的enabled值
            if (enabledObj instanceof Integer) {
                enabled = (Integer) enabledObj == 1;
            } else if (enabledObj instanceof Boolean) {
                enabled = (Boolean) enabledObj;
            } else {
                enabled = Boolean.valueOf(enabledObj.toString());
            }
            
            log.debug("接收到的enabled值: {} (类型: {})", enabledObj, enabledObj.getClass().getSimpleName());
            
            AdminMovieCategoryDTO updated = movieCategoryService.toggleCategoryStatus(id, enabled);
            if (updated == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "分类不存在"
                ));
            }
            
            log.debug("更新分类状态成功: {} -> {}", updated.getName(), enabled);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", updated,
                "message", String.format("分类已%s", enabled ? "启用" : "禁用")
            ));
        } catch (Exception e) {
            log.error("更新分类状态失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "更新分类状态失败，请稍后重试"
            ));
        }
    }
} 