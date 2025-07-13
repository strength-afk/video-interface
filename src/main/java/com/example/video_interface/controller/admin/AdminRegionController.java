package com.example.video_interface.controller.admin;

import com.example.video_interface.dto.admin.AdminRegionDTO;
import com.example.video_interface.service.admin.IAdminRegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理后台地区控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/regions")
@RequiredArgsConstructor
public class AdminRegionController {

    private final IAdminRegionService regionService;

    /**
     * 获取所有地区列表
     * @return 地区列表
     */
    @GetMapping("/list")
    public ResponseEntity<?> getAllRegions() {
        try {
            List<AdminRegionDTO> regions = regionService.getAllRegions();
            log.debug("获取地区列表成功，共{}个地区", regions.size());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", regions
            ));
        } catch (Exception e) {
            log.error("获取地区列表失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取地区列表失败，请稍后重试"
            ));
        }
    }

    /**
     * 获取地区详情
     * @param id 地区ID
     * @return 地区详情
     */
    @GetMapping("/detail")
    public ResponseEntity<?> getRegionById(@RequestParam Long id) {
        try {
            AdminRegionDTO region = regionService.getRegionById(id);
            if (region == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "地区不存在"
                ));
            }
            log.debug("获取地区详情成功: {}", region.getName());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", region
            ));
        } catch (Exception e) {
            log.error("获取地区详情失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "获取地区详情失败，请稍后重试"
            ));
        }
    }

    /**
     * 创建地区
     * @param regionDTO 地区信息
     * @return 创建后的地区
     */
    @PostMapping("/create")
    public ResponseEntity<?> createRegion(@RequestBody AdminRegionDTO regionDTO) {
        try {
            AdminRegionDTO created = regionService.createRegion(regionDTO);
            log.debug("创建地区成功: {}", created.getName());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", created,
                "message", "创建成功"
            ));
        } catch (Exception e) {
            log.error("创建地区失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "创建地区失败，请稍后重试"
            ));
        }
    }

    /**
     * 更新地区
     * @param regionDTO 地区信息
     * @return 更新后的地区
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateRegion(@RequestBody AdminRegionDTO regionDTO) {
        try {
            if (regionDTO.getId() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "地区ID不能为空"
                ));
            }
            
            AdminRegionDTO updated = regionService.updateRegion(regionDTO.getId(), regionDTO);
            if (updated == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "地区不存在"
                ));
            }
            
            log.debug("更新地区成功: {}", updated.getName());
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", updated,
                "message", "更新成功"
            ));
        } catch (Exception e) {
            log.error("更新地区失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "更新地区失败，请稍后重试"
            ));
        }
    }

    /**
     * 删除地区
     * @param requestBody 包含id的请求体
     * @return 操作结果
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteRegion(@RequestBody Map<String, Object> requestBody) {
        try {
            Long id = Long.valueOf(requestBody.get("id").toString());
            regionService.deleteRegion(id);
            log.debug("删除地区成功: {}", id);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "删除成功"
            ));
        } catch (Exception e) {
            log.error("删除地区失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "删除地区失败，请稍后重试"
            ));
        }
    }

    /**
     * 更新地区状态
     * @param requestBody 包含id和enabled的请求体
     * @return 更新后的地区
     */
    @PostMapping("/status")
    public ResponseEntity<?> toggleRegionStatus(@RequestBody Map<String, Object> requestBody) {
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
            
            AdminRegionDTO updated = regionService.toggleRegionStatus(id, enabled);
            if (updated == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", "地区不存在"
                ));
            }
            
            log.debug("更新地区状态成功: {} -> {}", updated.getName(), enabled);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", updated,
                "message", String.format("地区已%s", enabled ? "启用" : "禁用")
            ));
        } catch (Exception e) {
            log.error("更新地区状态失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", "更新地区状态失败，请稍后重试"
            ));
        }
    }
} 